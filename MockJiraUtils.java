package com.bigbrassband.jira.git;

import com.atlassian.jira.config.util.MockJiraHome;
import com.atlassian.jira.util.BuildUtilsInfoImpl;
import com.atlassian.jira.util.PathUtils;
import com.bigbrassband.jira.git.utils.JiraUtilsImpl;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by dmalyshkin on 07.10.2018.
 */
public class MockJiraUtils extends JiraUtilsImpl {
    public MockJiraUtils() {
        super(new BuildUtilsInfoImpl(), new MockJiraHome(PathUtils.joinPaths(
                System.getProperty("java.io.tmpdir"), RandomStringUtils.randomAlphanumeric(8))), new MockClusterManager());
    }

}
