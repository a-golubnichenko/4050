package com.bigbrassband.jira.git;

import com.atlassian.jira.mock.component.MockComponentWorker;

/**
 * Created by ababilo on 2/8/16.
 */
public class MockComponentAccessorHolder {

    private static volatile MockComponentWorker worker;

    public static MockComponentWorker getWorker() {
        MockComponentWorker w = worker;
        if (w == null) {
            w = new MockComponentWorker();
            worker = w;
        }
        return w;
    }
}
