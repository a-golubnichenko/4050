package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.ao.model.RepositoryParamsEntry;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

import java.beans.PropertyChangeListener;

public class MockRepositoryParamsEntry implements RepositoryParamsEntry {

    private Integer repoId;
    private String parameterName;
    private String parameterValue;

    @Override
    public Integer getRepositoryId() {
        return repoId;
    }

    @Override
    public void setRepositoryId(Integer repoId) {
        this.repoId = repoId;
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

    @Override
    public void setParameterName(String paramName) {
        this.parameterName = paramName;
    }

    @Override
    public String getParameterValue() {
        return parameterValue;
    }

    @Override
    public void setParameterValue(String value) {
        this.parameterValue = value;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void init() {
    }

    @Override
    public void save() {
    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    public <X extends RawEntity<Integer>> Class<X> getEntityType() {
        return null;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }

}
