package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.ao.dao.RepositoryParamsDao;
import com.bigbrassband.jira.git.ao.model.RepositoryParamsEntry;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MockRepositoryParamsDao implements RepositoryParamsDao {

    private Map<Integer, Map<String, RepositoryParamsEntry>> paramData = new HashMap<>();

    @Override
    public String getValue(int repoId, String paramName) {
        Map<String, RepositoryParamsEntry> repoEntries = paramData.get(repoId);
        if (repoEntries != null) {
            RepositoryParamsEntry entry = repoEntries.get(paramName);
            if (entry != null) {
                return entry.getParameterValue();
            }
        }
        return null;
    }

    @Override
    public void setValue(int repoId, String paramName, String value) {
        Map<String, RepositoryParamsEntry> repoEntry = paramData.computeIfAbsent(repoId, rId -> new HashMap<>());
        RepositoryParamsEntry entry = repoEntry.computeIfAbsent(paramName, pName -> new MockRepositoryParamsEntry());
        entry.setRepositoryId(repoId);
        entry.setParameterName(paramName);
        entry.setParameterValue(value);
    }

    @Override
    public void removeByRepoId(int repoId) {
        paramData.remove(repoId);
    }

    @Test
    public void testMockDao() {
        RepositoryParamsDao dao = new MockRepositoryParamsDao();
        dao.setValue(1, "param1", "valueA");
        Assert.assertEquals("valueA", dao.getValue(1, "param1"));
        Assert.assertNull(dao.getValue(2, "param1"));

        dao.setValue(2, "param1", "valueB");
        Assert.assertEquals("valueA", dao.getValue(1, "param1"));
        Assert.assertEquals("valueB", dao.getValue(2, "param1"));

        dao.setValue(2, "param2", "valueC");
        Assert.assertEquals("valueA", dao.getValue(1, "param1"));
        Assert.assertEquals("valueB", dao.getValue(2, "param1"));
        Assert.assertEquals("valueC", dao.getValue(2, "param2"));

        dao.setValue(2, "param1", "valueD");
        Assert.assertEquals("valueA", dao.getValue(1, "param1"));
        Assert.assertEquals("valueD", dao.getValue(2, "param1"));
        Assert.assertEquals("valueC", dao.getValue(2, "param2"));
        Assert.assertNull(dao.getValue(2, "param3"));

        dao.removeByRepoId(2);
        Assert.assertEquals("valueA", dao.getValue(1, "param1"));
        Assert.assertNull(dao.getValue(2, "param1"));
    }

}
