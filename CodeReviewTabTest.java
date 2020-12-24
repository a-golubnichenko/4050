package com.bigbrassband.jira.git;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issuetabpanel.GetActionsReply;
import com.atlassian.jira.plugin.issuetabpanel.GetActionsRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.util.Option;
import com.bigbrassband.jira.git.ao.model.PullRequestEntryV2;
import com.bigbrassband.jira.git.services.comments.PullRequestData;
import com.bigbrassband.jira.git.services.comments.ReviewManager;
import com.bigbrassband.jira.git.services.comments.reviewtab.CodeReviewService;
import com.bigbrassband.jira.git.services.comments.reviewtab.CodeReviewTabData;
import com.bigbrassband.jira.git.services.comments.reviewtab.CodeReviewTabPanel;
import com.bigbrassband.jira.git.services.gitmanager.GitManager;
import com.bigbrassband.jira.git.services.gitmanager.SingleGitManager;
import com.bigbrassband.jira.git.services.globalsettings.GlobalSettingsManager;
import com.bigbrassband.jira.git.services.globalsettings.MockGlobalSettingsManager;
import com.bigbrassband.jira.git.services.indexer.bbbpullrequests.PullRequestInfoIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.GitPluginIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionInfo;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionInfoNoCommit;
import com.bigbrassband.jira.git.services.issuetabpanels.ChangesHelper;
import com.bigbrassband.jira.git.services.issuetabpanels.DataObjectBasedIssueAction;
import com.bigbrassband.jira.git.services.issuetabpanels.changes.NoRevisionAction;
import com.bigbrassband.jira.git.services.issuetabpanels.summary.bean.TotalInfo;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManager;
import com.bigbrassband.jira.git.services.users.GitJiraUsersUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by ababilo on 8/31/15.
 */
@RunWith(JUnit4.class)
public class CodeReviewTabTest extends LocalRepositoryTest {

    private GitPluginPermissionManager gitPluginPermissionManager;
    private GitPluginIndexManager indexManager;
    private ReviewManager reviewManager;
    private CodeReviewTabPanel codeReviewTab;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        reviewManager = mock(ReviewManager.class);
        doReturn(null).when(reviewManager).findOpenRequest(anyInt(), anyString(), anyString());
        doReturn(true).when(reviewManager).isCodeReviewAllowed(any(GitManager.class));

        PluginLicenseManager licenseManager = mock(PluginLicenseManager.class);
        doReturn(Option.option(new MockPluginLicense())).when(licenseManager).getLicense();

        indexManager = mock(GitPluginIndexManager.class);
        doReturn(Collections.emptyList()).when(indexManager).getLogEntriesByIssues(anyCollection(), anyBoolean());

        ApplicationUser user = mock(ApplicationUser.class);
        doReturn("admin").when(user).getName();
        GitJiraUsersUtil gitJiraUsersUtil = mock(GitJiraUsersUtil.class);
        doReturn(user).when(gitJiraUsersUtil).getRemoteUserFromRequest(any(GetActionsRequest.class));

        PullRequestInfoIndexManager pullRequestInfoIndexManager = mock(PullRequestInfoIndexManager.class);
        doReturn(new TotalInfo()).when(pullRequestInfoIndexManager).getTotalInfoByIdAndRevision(anyInt(), anyString());

        gitPluginPermissionManager = mock(GitPluginPermissionManager.class);
        GlobalSettingsManager globalSettingsManager = MockGlobalSettingsManager.get();

        CodeReviewService codeReviewService = new CodeReviewService(
                null, reviewManager, manager, indexManager, new ChangesHelper(null, null, null, null, null, null, null, null, null, null, null, null, null) {
                    @Override
                    public void buildCodeReviewTabData(List<RevisionInfo> logEntries, Integer pullRequestId, String baseRevision,
                                                       String currentRevision, TotalInfo totalInfo) throws Exception {
                        // do nothing
                    }
                }, globalSettingsManager, pullRequestInfoIndexManager, null, gitPluginPermissionManager, new MockI18nHelper()) {
            @Override
            protected Collection<String> getIssueKeys(Issue issue) {
                return Collections.singletonList("TST-3");
            }
        };

        codeReviewTab = new CodeReviewTabPanel(null, licenseManager, null, manager, gitJiraUsersUtil, null, null, codeReviewService, null, null, null, null);
    }

    @Test
    public void showDeletedRepository() throws Exception {
        PullRequestData pullRequestData = new PullRequestData(1, 1, null, false, DEFAULT_REPO_ID,
                "second",
                "master",
                PullRequestEntryV2.State.OPEN, "author", new Date());
        doReturn(Collections.singletonList(pullRequestData)).when(reviewManager).getRequestsForIssue(any(Issue.class));

        this.repository.createBranch("second");
        GetActionsReply actionsReply = codeReviewTab.getActions(new GetActionsRequest(mock(Issue.class), null, false, false, null));
        Assert.assertEquals(1, actionsReply.actions().size());
        Assert.assertTrue(actionsReply.actions().get(0) instanceof DataObjectBasedIssueAction);
        // delete repository
        doAnswer(new Answer<SingleGitManager>() {

            @Override
            public SingleGitManager answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(manager).getGitManager(anyInt());
        actionsReply = codeReviewTab.getActions(new GetActionsRequest(mock(Issue.class), null, false, false, null));
        Assert.assertEquals(1, actionsReply.actions().size());
        Assert.assertTrue(actionsReply.actions().get(0) instanceof NoRevisionAction);
    }

    @Test
    public void firstPullRequestCreation_GIT_1814() throws Exception {
        // The first repo is created in a base class.

        // Creating a second repo
        ImmutablePair<LocalRepositoryManager, SingleGitManager> repoData = setupRepository(DEFAULT_REPO_ID + 1);
        LocalRepositoryManager repository2 = repoData.left;
        SingleGitManager singleGitManager2 = repoData.right;

        try {
            // Allow both repos for any user
            Arrays.asList(new SingleGitManager[]{singleGitManager2, singleGitManager});
            doReturn(Arrays.asList(new SingleGitManager[]{singleGitManager2, singleGitManager})).
                    when(gitPluginPermissionManager).getAccessedRepositoriesForUser(any(ApplicationUser.class));
            // Create mock log entry for the repo
            RevisionInfoNoCommit revInfo = new RevisionInfoNoCommit(singleGitManager.getId(), "12345", 1000, "", "", "", Collections.emptyList());
            doReturn(revInfo).when(indexManager).hasLogEntriesByIssues(anyCollection());

            // Get and check code review data
            GetActionsReply actionsReply = codeReviewTab.getActions(new GetActionsRequest(mock(Issue.class), null, false, false, null));
            Assert.assertEquals(1, actionsReply.actions().size());
            DataObjectBasedIssueAction action = (DataObjectBasedIssueAction) actionsReply.actions().get(0);
            CodeReviewTabData codeReviewTabData = (CodeReviewTabData) action.getDataObject();
            Assert.assertEquals(singleGitManager.getId(), codeReviewTabData.getLastRepoId());
        } finally {
            repository2.close();
        }
    }

    @Test
    public void secondPullRequestCreation_GIT_1814() throws Exception {
        // The first repo is created in a base class.

        // Prepare existing pull request to return from getRequestsForIssue
        PullRequestData pullRequestData = new PullRequestData(1, 1, null, false, DEFAULT_REPO_ID,
                "second",
                "master",
                PullRequestEntryV2.State.OPEN, "author", new Date());
        doReturn(Collections.singletonList(pullRequestData)).when(reviewManager).getRequestsForIssue(any(Issue.class));

        // Creating a second repo
        ImmutablePair<LocalRepositoryManager, SingleGitManager> repoData = setupRepository(DEFAULT_REPO_ID + 1);
        LocalRepositoryManager repository2 = repoData.left;
        SingleGitManager singleGitManager2 = repoData.right;

        try {
            // Allow both repos for any user
            Arrays.asList(new SingleGitManager[]{singleGitManager2, singleGitManager});
            doReturn(Arrays.asList(new SingleGitManager[]{singleGitManager2, singleGitManager})).
                    when(gitPluginPermissionManager).getAccessedRepositoriesForUser(any(ApplicationUser.class));
            // Create mock log entry for the repo
            RevisionInfoNoCommit revInfo = new RevisionInfoNoCommit(singleGitManager.getId(), "12345", 1000, "", "", "", Collections.emptyList());
            doReturn(revInfo).when(indexManager).hasLogEntriesByIssues(anyCollection());

            // Get and check code review data
            GetActionsReply actionsReply = codeReviewTab.getActions(new GetActionsRequest(mock(Issue.class), null, false, false, null));
            Assert.assertEquals(1, actionsReply.actions().size());
            DataObjectBasedIssueAction action = (DataObjectBasedIssueAction) actionsReply.actions().get(0);
            CodeReviewTabData codeReviewTabData = (CodeReviewTabData) action.getDataObject();
            Assert.assertEquals(singleGitManager.getId(), codeReviewTabData.getLastRepoId());
        } finally {
            repository2.close();
        }
    }

}
