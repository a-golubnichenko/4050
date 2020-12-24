package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.jiraservices.cluster.event.DummyEventService;
import com.bigbrassband.jira.git.jiraservices.cluster.event.EventService;
import com.bigbrassband.jira.git.jiraservices.cluster.event.EventServiceFactory;

/**
 * Created by isvirkina on 9/13/16.
 */
public class MockEventServiceFactory extends EventServiceFactory {

    public MockEventServiceFactory() {
        super(null, new MockClusterManager());
    }

    @Override
    public EventService getService() {
        return new DummyEventService();
    }
}
