package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.utils.I18nManager;

import java.io.IOException;

public class MockI18nManager extends I18nManager {


    public MockI18nManager() throws IOException {
        super(new MockI18nHelper(), new MockI18nResolver());
    }
}
