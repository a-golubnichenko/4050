package com.bigbrassband.jira.git;

import com.atlassian.jira.timezone.TimeZoneManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManager;
import com.bigbrassband.jira.git.services.users.GitJiraUsersUtilImpl;
import com.bigbrassband.jira.git.utils.UrlManager;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Created by ababilo on 6/10/16.
 */
public class MockGitJiraUsersUtil extends GitJiraUsersUtilImpl {

    public MockGitJiraUsersUtil(GitPluginPermissionManager gitPluginPermissionManager,
                                TimeZoneManager timeZoneManager,
                                UrlManager urlManager) {
        super(null, null, gitPluginPermissionManager, timeZoneManager, urlManager);
    }

    @Override
    public ApplicationUser getUserByEmail(String email) {
        return new MockApplicationUser(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(11), email);
    }

    @Override
    public String getUserAvatarUrl(ApplicationUser viewer, ApplicationUser user) {
        return "https://secure.gravatar.com/avatar/unknown?d=mm";
    }
}
