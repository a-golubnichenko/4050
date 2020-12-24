package com.bigbrassband.jira.git;

/**
 * @author isvirkina
 */
public class MockJiraUtilsWithCodeReviewEnabled extends MockJiraUtils {

    @Override
    public boolean isGitServerEnabled() {
        return true;
    }

    @Override
    public boolean isCodeReviewFeatureEnabled() {
        return true;
    }
};