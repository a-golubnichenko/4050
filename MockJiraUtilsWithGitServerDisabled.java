package com.bigbrassband.jira.git;

/**
 * @author isvirkina
 */
public class MockJiraUtilsWithGitServerDisabled extends MockJiraUtils {

    @Override
    public boolean isGitServerEnabled() {
        return false;
    }
};