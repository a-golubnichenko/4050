package com.bigbrassband.jira.git;

import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.atlassian.mock.propertyset.MockPropertySet;
import com.opensymphony.module.propertyset.PropertySet;

import javax.annotation.Nonnull;

/**
 * Created by ababilo on 04.06.16.
 */
public class MockJiraPropertySetFactory implements JiraPropertySetFactory {

    @Nonnull
    @Override
    public PropertySet buildNoncachingPropertySet(String name) {
        return new MockPropertySet();
    }

    @Nonnull
    @Override
    public PropertySet buildNoncachingPropertySet(String name, Long id) {
        return new MockPropertySet();
    }

    @Nonnull
    @Override
    public PropertySet buildCachingDefaultPropertySet(String name, boolean bulkLoad) {
        return new MockPropertySet();
    }

    @Nonnull
    @Override
    public PropertySet buildCachingPropertySet(String name, Long id, boolean bulkLoad) {
        return new MockPropertySet();
    }

    @Nonnull
    @Override
    public PropertySet buildCachingDefaultPropertySet(String name) {
        return new MockPropertySet();
    }

    @Nonnull
    @Override
    public PropertySet buildCachingPropertySet(String name, Long id) {
        return new MockPropertySet();
    }

    @Nonnull
    @Override
    public PropertySet buildCachingPropertySet(PropertySet propertySet, boolean bulkLoad) {
        return new MockPropertySet();
    }

    @Nonnull
    @Override
    public PropertySet buildMemoryPropertySet(String name, Long id) {
        return new MockPropertySet();
    }
}
