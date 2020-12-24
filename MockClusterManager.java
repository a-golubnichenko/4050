package com.bigbrassband.jira.git;

import com.atlassian.jira.cluster.ClusterManager;
import com.atlassian.jira.cluster.ClusterStateException;
import com.atlassian.jira.cluster.Node;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by ababilo on 6/9/16.
 */
public class MockClusterManager implements ClusterManager {

    @Override
    public void checkIndex() {

    }

    @Nullable
    @Override
    public String getNodeId() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isClustered() {
        return false;
    }

    @Override
    public Set<Node> getAllNodes() {
        return null;
    }

    @Override
    public boolean isClusterLicensed() {
        return false;
    }

    @Override
    public void requestCurrentIndexFromNode(String s) {

    }

    public Collection<Node> findLiveNodes() {
        return null;
    }

    public void refreshLiveNodes() {
    }

    public void removeIfOffline(String s) throws ClusterStateException {
    }

    public List<String> removeOfflineNodesIfOlderThan(@NotNull Duration duration) {
        return null;
    }

    public void moveToOffline(String s) throws ClusterStateException {
    }

    public List<String> moveNodesToOfflineIfOlderThan(@NotNull Duration duration) {
        return null;
    }

    public boolean isNodeAlive(String s) {
        return false;
    }

    public boolean isNodePresent(String s) {
        return false;
    }

    public boolean isNodeOffline(String s) {
        return false;
    }

    public boolean isNodeActive(@NotNull String s) {
        return false;
    }

    public List<Node> findActiveAndNotAliveNodes() {
        return null;
    }

    public List<Node> findOfflineNodes() {
        return null;
    }

}
