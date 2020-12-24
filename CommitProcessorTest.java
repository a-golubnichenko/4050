package com.bigbrassband.jira.git;

import com.atlassian.jira.cluster.ClusterManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.timezone.TimeZoneManager;
import com.atlassian.jira.user.ApplicationUser;
import com.bigbrassband.jira.git.ao.MockGitRepository;
import com.bigbrassband.jira.git.ao.MockUntrackedCommitNotificationDao;
import com.bigbrassband.jira.git.ao.dao.ObjectRegistrationDao;
import com.bigbrassband.jira.git.ao.dao.UntrackedCommitNotificationDao;
import com.bigbrassband.jira.git.exceptions.GitPluginException;
import com.bigbrassband.jira.git.jiraservices.cluster.event.EventServiceFactory;
import com.bigbrassband.jira.git.services.gitmanager.SingleGitManager;
import com.bigbrassband.jira.git.services.globalsettings.GlobalSettingsManager;
import com.bigbrassband.jira.git.services.globalsettings.MockGlobalSettingsManager;
import com.bigbrassband.jira.git.services.indexer.files.FilesInfoIndexManager;
import com.bigbrassband.jira.git.services.indexer.pullrequests.MergeRequestIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.CommitIssueCollector;
import com.bigbrassband.jira.git.services.indexer.revisions.CommitProcessor;
import com.bigbrassband.jira.git.services.indexer.revisions.GitPluginIndexManagerImpl;
import com.bigbrassband.jira.git.services.indexer.revisions.MockRevisionsIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.ReindexProgressMonitor;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionInfo;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionsIndexManagerImpl;
import com.bigbrassband.jira.git.services.issue.UpdateIssueService;
import com.bigbrassband.jira.git.services.notifications.NotificationManager;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManager;
import com.bigbrassband.jira.git.services.props.GProperties;
import com.bigbrassband.jira.git.services.scripting.ScriptCommitProcessor;
import com.bigbrassband.jira.git.services.scripting.ScriptTriggersHive;
import com.bigbrassband.jira.git.services.smartcommits.SmartCommitsProcessor;
import com.bigbrassband.jira.git.utils.Clock;
import com.bigbrassband.jira.git.utils.JiraEmailUtils;
import com.bigbrassband.jira.git.utils.SystemClock;
import com.bigbrassband.jira.git.utils.UrlManager;
import junit.framework.Assert;
import org.apache.commons.mail.EmailException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by nchernov on 02-Jun-16.
 */
public class CommitProcessorTest extends LocalRepositoryTest {

    private static final String COMMIT1_BYTES = "tree 84be2505a7bebc26cddad0997174bf72fa623ad3\n" +
            "author AndreyLevchenko <levchenko.andrey@gmail.com> %s +0600\n" +
            "committer AndreyLevchenko <levchenko.andrey@gmail.com> %s +0600\n" +
            "\n" +
            "TST-1 Initial commit\n";
    private CommitIssueCollector commitProcessor;
    private GitPluginIndexManagerImpl gitPluginIndexManager;
    private RevisionsIndexManagerImpl indexManager;
    private NotificationManager notificationManager;
    private JiraEmailUtils jiraEmailUtils = mock(JiraEmailUtils.class);
    private GitPluginPermissionManager permissionManager;
    private MockGitJiraUsersUtil gitJiraUsersUtil;
    private GlobalSettingsManager globalSettingsManager = MockGlobalSettingsManager.get();
    private MockUntrackedCommitNotificationDao untrackedCommitNotificationDao = new MockUntrackedCommitNotificationDao();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        permissionManager = MockPermissionManager.allPermissionsManager(currentUser);

        when(globalSettingsManager.getSendCommitNotificationEmails()).thenReturn(Boolean.TRUE);

        gitJiraUsersUtil = new MockGitJiraUsersUtil(permissionManager, mock(TimeZoneManager.class), mock(UrlManager.class));

        notificationManager = MockNotificationManager.get(manager, issueManager, currentUser, permissionManager,
                jiraEmailUtils, gitJiraUsersUtil, new SystemClock());
        commitProcessor = new CommitIssueCollector(null, untrackedCommitNotificationDao, globalSettingsManager, mock(Clock.class));

        indexManager = MockRevisionsIndexManager.get(manager, issueManager, permissionManager);
    }

    @Test
    /**
     * Test for bug GIT-1689
     */
    public void sameUntrackedCommitIndexedTwice() throws IOException, GitAPIException, IndexException, URISyntaxException, MessagingException, EmailException {
        gitPluginIndexManager = new GitPluginIndexManagerImpl(mock(FilesInfoIndexManager.class), indexManager,
                mock(MergeRequestIndexManager.class), manager,
                mock(SmartCommitsProcessor.class), mock(ScriptCommitProcessor.class),
                mock(UpdateIssueService.class), notificationManager,
                new MockI18nManager(),
                null, untrackedCommitNotificationDao, globalSettingsManager,
                mock(ObjectRegistrationDao.class), mock(ScriptTriggersHive.class),
                mock(EventServiceFactory.class), mock(Clock.class), mock(ClusterManager.class));
        SingleGitManager mockGitManager = manager.getGitManager(DEFAULT_REPO_ID);
        mockGitManager.setInitDate(new Date(System.currentTimeMillis() - 1000L));
        this.repository.addEmptyFile("file");
        commit("Untracked commit");

        gitPluginIndexManager.updateIndex(mockGitManager, new ReindexProgressMonitor());
        verify(jiraEmailUtils, never()).sendMail(any(ApplicationUser.class), anyString(), anyString(), anyMap(), anyString()); // no email -- first indexing
        gitPluginIndexManager.removeEntriesByBranch(mockGitManager.getId(), "master");
        manager.clearLastIndexedRevisionsForARepo(mockGitManager);
        gitPluginIndexManager.updateIndex(mockGitManager, new ReindexProgressMonitor());
        verify(jiraEmailUtils, never()).sendMail(any(ApplicationUser.class), anyString(), anyString(), anyMap(), anyString()); // no email -- first indexing
    }

    @Test
    /**
     * Test for bug GIT-1704
     */
    public void testCommitForNewRepository() throws GitPluginException {
        GProperties mockRepoProperties = new MockGitRepository();
        mockRepoProperties.setOrigin(repository.getRoot());
        mockRepoProperties.setRootToAbsolutePath(repository.getRoot());
        mockRepoProperties.setEnableFetches(false);
        mockRepoProperties.setId(1);
        SingleGitManager newRepo = manager.setupRepository(mockRepoProperties, new ArrayList<Long>(1), null);

        Date freshCommitDate = new Date(new Date().getTime() + 10000L);
        RevCommit revCommit = RevCommit.parse(String.format(COMMIT1_BYTES, Long.toString(freshCommitDate.getTime() / 1000L),
                Long.toString(freshCommitDate.getTime() / 1000L)).getBytes());
        commitProcessor.processLogEntry(revCommit, "TST-1", newRepo,
                new CommitProcessor.CommitProperties(true, true, false));
        Collection<List<RevisionInfo>> revisions = commitProcessor.getLogEntriesByIssueKeys().values();
        Assert.assertTrue("No commits has been processed", revisions.size() > 0);
        List<RevisionInfo> revisionInfos = revisions.iterator().next();
        Assert.assertTrue("1 Commit expected to be processed but was: " + revisionInfos.size(), revisionInfos.size() == 1);
        RevCommit actualRevCommit = revisionInfos.get(0).getCommit();
        Assert.assertEquals(revCommit, actualRevCommit);
    }

    @Test // GIT-1749
    public void ignoreIssueUpdateErrors() throws Exception {
        GlobalSettingsManager mock = MockGlobalSettingsManager.get();
        when(mock.getTouchIssueLastUpdatedDate()).thenReturn(Boolean.TRUE);

        IssueIndexManager issueIndexManager = mock(IssueIndexManager.class);
        when(issueIndexManager.reIndexIssueObjects(anyCollection())).thenThrow(new RuntimeException("!!!"));

        IssueManager issueManager = mock(IssueManager.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                throw new RuntimeException("!!!");
            }
        });

        UpdateIssueService updateIssueService = new UpdateIssueService(issueManager, permissionManager, issueIndexManager, mock,
                gitJiraUsersUtil);

        gitPluginIndexManager = new GitPluginIndexManagerImpl(mock(FilesInfoIndexManager.class), indexManager,
                mock(MergeRequestIndexManager.class), manager,
                mock(SmartCommitsProcessor.class), mock(ScriptCommitProcessor.class),
                updateIssueService, notificationManager,
                new MockI18nManager(),
                null, mock(UntrackedCommitNotificationDao.class),
                globalSettingsManager,
                mock(ObjectRegistrationDao.class), mock(ScriptTriggersHive.class),
                mock(EventServiceFactory.class), mock(Clock.class), mock(ClusterManager.class));

        SingleGitManager repository = manager.getGitManager(DEFAULT_REPO_ID);
        gitPluginIndexManager.updateIndex(repository, new ReindexProgressMonitor());

        this.repository.addEmptyFile("file");
        commit("TST-3 Added file");

        gitPluginIndexManager.updateIndex(repository, new ReindexProgressMonitor());
    }
}
