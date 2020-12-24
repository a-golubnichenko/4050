package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.ao.dao.RepositoryWatcherDao;
import com.bigbrassband.jira.git.ao.model.RepoWatcherEntry;
import net.java.ao.EntityManager;
import net.java.ao.EntityStreamCallback;
import net.java.ao.RawEntity;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author dmalyshkin, 2016-10-12.
 */
public class MockRepositoryWatcherDaoImpl implements RepositoryWatcherDao {

    private Map<Integer, Set<String>> database = new HashMap<>();

    @Override
    public void streamByRepositoryId(Integer repoId, final EntityStreamCallback<RepoWatcherEntry, Integer> callback) {
        // Do nothing
    }

    @Override
    public int getCountForRepositoryId(Integer repoId) {
        return 0;
    }

    @Override
    public RepoWatcherEntry getByRepositoryIdAndUserId(final Integer repoId, final String userId) {
        Set<String> users = database.get(repoId);
        if (users != null && users.contains(userId)) {
            return new RepoWatcherEntry() {
                public void setRepositoryId(Integer repoId) {}
                public Integer getRepositoryId() { return repoId; }
                public void setUserId(String userId) {}
                public String getUserId() { return userId; }
                public int getID() { return 0; }
                public void init() {}
                public void save() {}
                public EntityManager getEntityManager() { return null; }
                public <X extends RawEntity<Integer>> Class<X> getEntityType() { return null; }
                public void addPropertyChangeListener(PropertyChangeListener var1) {}
                public void removePropertyChangeListener(PropertyChangeListener var1) {}
            };
        }
        return null;
    }

    @Override
    public void create(Integer repoId, String userId) {
        Set<String> users = database.computeIfAbsent(repoId, k -> new HashSet<>());
        users.add(userId);
    }

    @Override
    public void delete(RepoWatcherEntry entry) {
        Set<String> users = database.get(entry.getRepositoryId());
        if (users != null) {
            users.remove(entry.getUserId());
        }
    }

    @Override
    public void deleteByRepositoryId(Integer repoId) {
        database.remove(repoId);
    }

}
