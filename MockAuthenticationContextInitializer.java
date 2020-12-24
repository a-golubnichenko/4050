package com.bigbrassband.jira.git;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.security.MockSimpleAuthenticationContext;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public class MockAuthenticationContextInitializer {

    public static void putDefaultMockToComponentAccessor() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ApplicationUser user = Mockito.mock(ApplicationUser.class);
        Mockito.when(user.getName()).thenReturn("admin");

        MockSimpleAuthenticationContext mockContext = new MockSimpleAuthenticationContext(user,
                Locale.ENGLISH, new com.atlassian.jira.mock.i18n.MockI18nHelper());

        ComponentAccessor.initialiseWorker(MockComponentAccessorHolder.getWorker().addMock(JiraAuthenticationContext.class, mockContext));
    }
}
