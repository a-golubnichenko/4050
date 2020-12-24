package com.bigbrassband.jira.git;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.UserLocaleStore;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

/**
 * Mock for I18nBean usage
 * Created by ababilo on 12/25/15.
 */
public class MockUserLocaleStore {

    public static void putDefaultMockToComponentAccessor(Locale locale) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        com.atlassian.jira.user.MockUserLocaleStore mock = new com.atlassian.jira.user.MockUserLocaleStore(locale);
        ComponentAccessor.initialiseWorker(MockComponentAccessorHolder.getWorker().addMock(UserLocaleStore.class, mock));
    }
}
