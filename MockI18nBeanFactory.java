package com.bigbrassband.jira.git;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.bean.MockI18nBean;

/**
 * Created by ababilo on 2/8/16.
 */
public class MockI18nBeanFactory {

    public static void putMock() {
        ComponentAccessor.initialiseWorker(MockComponentAccessorHolder.getWorker().addMock(I18nHelper.BeanFactory.class, new MockI18nBean.MockI18nBeanFactory()));
    }
}
