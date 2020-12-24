package com.bigbrassband.jira.git;

import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.user.ApplicationUser;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManager;
import com.bigbrassband.jira.git.ao.dao.CodeReviewDaoImpl;
import com.bigbrassband.jira.git.ao.dao.SmartCommitsDaoImpl;
import com.bigbrassband.jira.git.ao.model.ReviewCommentV2;
import com.bigbrassband.jira.git.ao.model.SCRegistration;
import com.bigbrassband.jira.git.services.gitmanager.MultipleGitRepositoryManager;
import com.bigbrassband.jira.git.services.gitmanager.SingleGitManager;
import com.bigbrassband.jira.git.services.issuetabpanels.ChangesHelper;
import com.bigbrassband.jira.git.services.notifications.NotificationManager;
import com.bigbrassband.jira.git.services.notifications.WatchersProvider;
import com.bigbrassband.jira.git.rest.UserProfileManager;
import com.bigbrassband.jira.git.services.indexer.revisions.GitPluginIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionInfo;
import com.bigbrassband.jira.git.services.users.GitJiraUsersUtil;
import com.bigbrassband.jira.git.utils.Clock;
import com.bigbrassband.jira.git.utils.JiraEmailUtils;
import com.bigbrassband.jira.git.utils.UrlManager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Created by ababilo on 16.06.16.
 */
public class MockNotificationManager {

    public static NotificationManager get(MultipleGitRepositoryManager manager,
                                          IssueManager issueManager,
                                          final ApplicationUser currentUser,
                                          GitPluginPermissionManager permissionManager,
                                          JiraEmailUtils emailUtils,
                                          GitJiraUsersUtil gitJiraUsersUtil,
                                          Clock clock) {
        return new NotificationManager(new WatchersProvider(null, null, null) {
            @Override
            public Collection<ApplicationUser> getRepoSubscribers(Issue issue, SingleGitManager gitManager) {
                return Collections.singletonList(currentUser);
            }
        }, issueManager, permissionManager, emailUtils, gitJiraUsersUtil, manager, new CodeReviewDaoImpl(null) {
            @Override
            public List<ReviewCommentV2> listCommentsByCommitId(String commitId) {
                return Collections.emptyList();
            }
        }, mock(UrlManager.class), null, new UserProfileManager(null, null) {
            @Override
            public boolean getSendCommitEmails(ApplicationUser user) {
                return true;
            }
        }, new ChangesHelper(manager, gitJiraUsersUtil, null, null, null, null, null, null, null, null, null, null, null),
        mock(DateTimeFormatter.class), clock, new SmartCommitsDaoImpl(null, new MockClusterLockManager()) {
            @Override
            public SCRegistration getSmartCommit(String revision, Integer revisionTime) {
                return null;
            }
        }) {
            @Override
            protected List<RevisionInfo> filterCommitListForWatcher(List<RevisionInfo> collectedCommits, ApplicationUser watcher, SingleGitManager gitManager, GitPluginIndexManager gitPluginIndexManager, Issue issue) {
                return collectedCommits;
            }
        };
    }
}
