package com.bigbrassband.jira.git;

/**
 * @author dmalyshkin
 */
public class MockJiraUtilsWithLocalPathTrue extends MockJiraUtils {

    @Override
    public boolean isPathInsideJiraHome(String root) {
        return true;
    }

}
