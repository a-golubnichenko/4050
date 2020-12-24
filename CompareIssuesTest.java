package com.bigbrassband.jira.git;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.cluster.ClusterManager;
import com.atlassian.jira.timezone.TimeZoneManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.MessageSetImpl;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.query.QueryImpl;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.bigbrassband.jira.git.ao.MockUntrackedCommitNotificationDao;
import com.bigbrassband.jira.git.ao.dao.ObjectRegistrationDao;
import com.bigbrassband.jira.git.jiraservices.cluster.event.EventServiceFactory;
import com.bigbrassband.jira.git.jiraservices.compatibility.CompatibilityIssueSearchService;
import com.bigbrassband.jira.git.services.BuildProperties;
import com.bigbrassband.jira.git.services.comments.ReviewManager;
import com.bigbrassband.jira.git.services.gitmanager.MultipleGitRepositoryManager;
import com.bigbrassband.jira.git.services.gitmanager.SingleGitManager;
import com.bigbrassband.jira.git.services.gitviewer.compare.CompareIssuesGitAction;
import com.bigbrassband.jira.git.services.gitviewer.compare.IssueInfoProvider;
import com.bigbrassband.jira.git.services.gitviewer.management.FavouritesRepoManager;
import com.bigbrassband.jira.git.services.gitviewer.management.UserPatsManager;
import com.bigbrassband.jira.git.services.gitviewer.menu.UserRepoHistoryManager;
import com.bigbrassband.jira.git.services.globalsettings.GlobalSettingsManager;
import com.bigbrassband.jira.git.services.globalsettings.MockGlobalSettingsManager;
import com.bigbrassband.jira.git.services.indexer.files.FilesInfoIndexManager;
import com.bigbrassband.jira.git.services.indexer.pullrequests.MergeRequestIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.GitPluginIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.GitPluginIndexManagerImpl;
import com.bigbrassband.jira.git.services.indexer.revisions.MockRevisionsIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.ReindexProgressMonitor;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionIndexer;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionsIndexManagerImpl;
import com.bigbrassband.jira.git.services.issue.UpdateIssueService;
import com.bigbrassband.jira.git.services.issuetabpanels.ChangesHelper;
import com.bigbrassband.jira.git.services.notifications.NotificationManager;
import com.bigbrassband.jira.git.services.notifications.RepositoryWatchManager;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManager;
import com.bigbrassband.jira.git.services.scripting.ScriptCommitProcessor;
import com.bigbrassband.jira.git.services.scripting.ScriptTriggersHive;
import com.bigbrassband.jira.git.services.smartcommits.SmartCommitsProcessor;
import com.bigbrassband.jira.git.services.users.GitJiraUsersUtil;
import com.bigbrassband.jira.git.services.users.frontendsettings.UserFrontendSettingsManager;
import com.bigbrassband.jira.git.utils.Clock;
import com.bigbrassband.jira.git.utils.JiraEmailUtils;
import com.bigbrassband.jira.git.utils.SystemClock;
import com.bigbrassband.jira.git.utils.UrlManager;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by nchernov on 01-Nov-16.
 */
public class CompareIssuesTest extends LocalRepositoryTest {

    private GitPluginIndexManagerImpl gitPluginIndexManager;
    private RevisionsIndexManagerImpl indexManager;
    private NotificationManager notificationManager;
    private JiraEmailUtils jiraEmailUtils = mock(JiraEmailUtils.class);
    private GitPluginPermissionManager permissionManager;
    private MockGitJiraUsersUtil gitJiraUsersUtil;
    private GlobalSettingsManager globalSettingsManager = MockGlobalSettingsManager.get();
    private MockUntrackedCommitNotificationDao untrackedCommitNotificationDao = new MockUntrackedCommitNotificationDao();
    private final SearchService searchService = mock(SearchService.class);
    private final CompatibilityIssueSearchService compatibilityIssueSearchService = mock(CompatibilityIssueSearchService.class);
    private static class TestCompareIssuesGitAction extends CompareIssuesGitAction {
        public TestCompareIssuesGitAction(PluginLicenseManager licenseManager, MultipleGitRepositoryManager multipleGitRepositoryManager,
                                          GitPluginIndexManager gitPluginIndexManager, RevisionIndexer revisionIndexer,
                                          GitPluginPermissionManager gitPluginPermissionManager, TimeZoneManager timeZoneManager,
                                          ChangesHelper changesHelper, GitJiraUsersUtil gitJiraUsersUtil,
                                          UserFrontendSettingsManager userFrontendSettingsManager,
                                          UserRepoHistoryManager userRepoHistoryManager, ReviewManager reviewManager,
                                          FavouritesRepoManager favouritesRepoManager, UserPatsManager userPatsManager,
                                          RepositoryWatchManager repositoryWatchManager,
                                          BuildProperties buildProperties, PluginAccessor pluginAccessor,
                                          IssueService issueService, I18nHelper i18n,
                                          SearchService searchService,
                                          CompatibilityIssueSearchService compatibilityIssueSearchService) {
            super(licenseManager, multipleGitRepositoryManager, gitPluginIndexManager, revisionIndexer,
                    gitPluginPermissionManager, timeZoneManager, changesHelper, gitJiraUsersUtil, userFrontendSettingsManager,
                    userRepoHistoryManager, reviewManager, favouritesRepoManager, userPatsManager,
                    repositoryWatchManager, buildProperties,
                    pluginAccessor, issueService, i18n, searchService, compatibilityIssueSearchService, null, null);
        }
        public void doValidation() {
            super.doValidation();
        }
        public Locale getLocale() {
            return Locale.getDefault();
        }
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        permissionManager = MockPermissionManager.allPermissionsManager(currentUser);

        when(globalSettingsManager.getSendCommitNotificationEmails()).thenReturn(Boolean.TRUE);

        gitJiraUsersUtil = new MockGitJiraUsersUtil(permissionManager, mock(TimeZoneManager.class), mock(UrlManager.class));

        notificationManager = MockNotificationManager.get(manager, issueManager, currentUser, permissionManager,
                jiraEmailUtils, gitJiraUsersUtil, new SystemClock());

        indexManager = MockRevisionsIndexManager.get(manager, issueManager, permissionManager);
        dao.getById(DEFAULT_REPO_ID).setGitViewerEnabled(true);
        gitPluginIndexManager = new GitPluginIndexManagerImpl(mock(FilesInfoIndexManager.class), indexManager,
                mock(MergeRequestIndexManager.class), manager,
                mock(SmartCommitsProcessor.class), mock(ScriptCommitProcessor.class),
                mock(UpdateIssueService.class), notificationManager,
                new MockI18nManager(),
                null, untrackedCommitNotificationDao, globalSettingsManager,
                mock(ObjectRegistrationDao.class), mock(ScriptTriggersHive.class),
                mock(EventServiceFactory.class), mock(Clock.class), mock(ClusterManager.class));

    }

    @Test
    public void partialCommitsAssociatedWithExistingIssues() throws Exception {
        List<String> expectedIssueKeys = Arrays.asList("TST-1", "TST-4", "TST-3");
        repository.createBranch("TST-4");
        repository.checkoutBranch("TST-4");
        repository.addEmptyFile("file");
        commit("Untracked commit");
        repository.addEmptyFile("file2");
        commit("TST-1 TST-2 2 issues");
        repository.addEmptyFile("file3");
        commit("TST-3 another one issue");
        repository.addEmptyFile("file4");
        commit("TST-100500 no such issue yet");
        repository.checkoutBranch("master");
        assertAction("issuekey in (TST-1,TST-2,TST-3,TST-100500)", "issuekey in (TST-1,TST-4,TST-3) order by issuekey asc", expectedIssueKeys, "master", "TST-4");
    }

    @Test
    public void oneIssueOneCommit() throws Exception {
        List<String> expectedIssueKeys = Arrays.asList("TST-1");
        repository.createBranch("TST-4");
        repository.checkoutBranch("TST-4");
        repository.addEmptyFile("file");
        commit("TST-1 issue");
        repository.checkoutBranch("master");
        assertAction("issuekey in (TST-1)", "issuekey in (TST-1) order by issuekey asc", expectedIssueKeys, "master", "TST-4");
    }

    @Test
    public void oneIssueMultipleCommits() throws Exception {
        List<String> expectedIssueKeys = Arrays.asList("TST-1");
        repository.createBranch("TST-4");
        repository.checkoutBranch("TST-4");
        repository.addEmptyFile("file");
        commit("TST-1 issue");
        repository.addEmptyFile("file2");
        commit("TST-1 issue issue");
        repository.checkoutBranch("master");
        assertAction("issuekey in (TST-1)", "issuekey in (TST-1) order by issuekey asc", expectedIssueKeys, "master", "TST-4");
    }

    private void assertAction(String jql, String finalJql, List<String> expectedIssueKeys, String baseBranch, String compareBranch) throws Exception {
        SingleGitManager mockGitManager = manager.getGitManager(DEFAULT_REPO_ID);
        gitPluginIndexManager.updateIndex(mockGitManager, new ReindexProgressMonitor());
        TestCompareIssuesGitAction compareIssuesGitAction = new TestCompareIssuesGitAction(
                MockPluginLicenseManager.DEFAULT_PLUGIN_LICENSE_MANAGER, manager,
                gitPluginIndexManager, mock(RevisionIndexer.class), permissionManager, mock(TimeZoneManager.class), mock(ChangesHelper.class),
                gitJiraUsersUtil, mock(UserFrontendSettingsManager.class), mock(UserRepoHistoryManager.class),
                mock(ReviewManager.class), mock(FavouritesRepoManager.class), mock(UserPatsManager.class),
                mock(RepositoryWatchManager.class),
                mock(BuildProperties.class), mock(PluginAccessor.class), mock(IssueService.class),
                mock(I18nHelper.class), searchService, compatibilityIssueSearchService);
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        MessageSetImpl mockMessageSet = mock(MessageSetImpl.class);
        when(searchService.parseQuery(any(ApplicationUser.class), queryCaptor.capture()))
                .thenReturn(new SearchService.ParseResult(new QueryImpl(), mockMessageSet));
        when(compatibilityIssueSearchService.searchIssues(any(ApplicationUser.class), any(SearchService.ParseResult.class)))
                .thenReturn(createMockIssues(expectedIssueKeys));
        compareIssuesGitAction.setBaseBranchName(baseBranch);
        compareIssuesGitAction.setBranchName(compareBranch);
        compareIssuesGitAction.setRepoId(DEFAULT_REPO_ID);
        compareIssuesGitAction.doValidation();
        compareIssuesGitAction.doExecute();
        List<IssueInfoProvider.IssueInfo> issues = compareIssuesGitAction.getIssues();
        Assert.assertEquals(expectedIssueKeys.size(), issues.size());

        int count = expectedIssueKeys.size();
        for(IssueInfoProvider.IssueInfo issue: issues) {
            if (!expectedIssueKeys.contains(issue.getIssueKey())) {
                Assert.fail(String.format("Issue %s not expected in result", issue.getIssueKey()));
            } else {
                count--;
            }
        }
        Assert.assertEquals("Not all expected issues are present in result. Expected are: " + expectedIssueKeys, 0, count);
        List<String> expected = Arrays.asList(jql.substring(jql.indexOf('(') + 1, jql.indexOf(')')).split(",", -1));
        Set<String> expectedSet = new HashSet<>(expected);
        Assert.assertTrue(expectedSet.size() == expected.size());
        List<String> actual = Arrays.asList(jql.substring(queryCaptor.getValue().indexOf('(') + 1, jql.indexOf(')')).split(",", -1));
        Set<String> actualSet = new HashSet<>(actual);
        Assert.assertTrue(actualSet.size() == actual.size());
        Assert.assertEquals(expectedSet, actualSet);
        Assert.assertEquals(finalJql, compareIssuesGitAction.getJql());
    }
}
