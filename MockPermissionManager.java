package com.bigbrassband.jira.git;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManager;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.anyString;

/**
 * @author isvirkina
 */
public class MockPermissionManager {

    public static GitPluginPermissionManager nonePermissionManager(ApplicationUser currentUser) {
        GitPluginPermissionManager permissionManager = Mockito.mock(GitPluginPermissionManager.class);
        Mockito.doReturn(false).when(permissionManager).hasReadAccessByRepository(anyInt(), any(ApplicationUser.class));
        Mockito.doReturn(currentUser).when(permissionManager).getCurrentUser();
        return permissionManager;
    }

    public static GitPluginPermissionManager allPermissionsManager(ApplicationUser currentUser) {
        GitPluginPermissionManager permissionManager = Mockito.mock(GitPluginPermissionManager.class);
        Mockito.doReturn(true).when(permissionManager).hasReadAccessByRepository(anyInt(), any(ApplicationUser.class));
        Mockito.doReturn(true).when(permissionManager).hasAdminAccess(any(ApplicationUser.class));
        Mockito.doReturn(true).when(permissionManager).hasAdminAccess();
        Mockito.doReturn(true).when(permissionManager).hasReadAccess(any(Issue.class), any(ApplicationUser.class));
        Mockito.doReturn(true).when(permissionManager).hasReadAccess(anyString(), any(ApplicationUser.class));
        Mockito.doReturn(true).when(permissionManager).hasReadAccess(any(ApplicationUser.class));
        Mockito.doReturn(currentUser).when(permissionManager).getCurrentUser();
        return permissionManager;
    }
}
