package com.bigbrassband.jira.git;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;

import java.util.Map;

/**
 * Created by ababilo on 12/7/15.
 */
public class MockApplicationProperties {

    public static void putDefaultMockToComponentAccessor(Map<String, Object> props) {
        com.atlassian.jira.mock.MockApplicationProperties mock = new com.atlassian.jira.mock.MockApplicationProperties(props);

        ComponentAccessor.initialiseWorker(MockComponentAccessorHolder.getWorker().addMock(ApplicationProperties.class, mock));
    }
}
