package com.bigbrassband.jira.git;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManagerImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author isvirkina
 */
public class GitPluginPermissionManagerTest {

    GitPluginPermissionManagerImpl permissionManager;

    @Before
    public void setUp() {
        permissionManager = new GitPluginPermissionManagerImpl(null, null, null, null, null, null, null, null, null);
    }


    @Test
    public void testAnonymousAccess() {
        ApplicationUser anonymousUser = null;
        Issue someIssue = null;
        boolean res = permissionManager.hasReadAccess(someIssue, anonymousUser);
        Assert.assertFalse(res);
    }
}
