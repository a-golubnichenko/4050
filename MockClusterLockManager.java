package com.bigbrassband.jira.git;

import com.atlassian.beehive.ClusterLock;
import com.bigbrassband.jira.git.jiraservices.cluster.ClusterLockManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ababilo
 */
public class MockClusterLockManager extends ClusterLockManager {

    public MockClusterLockManager() {
        super(null);
    }

    @Override
    public ClusterLock getLock(String name) {
        final ReentrantLock lock = new ReentrantLock();
        return new ClusterLock() {

            @Override
            public boolean isHeldByCurrentThread() {
                return false;
            }

            @Override
            public Condition newCondition() {
                return null;
            }

            @Override
            public void lock() {
                lock.lock();
            }

            @Override
            public void lockInterruptibly() throws InterruptedException {
                lock.lockInterruptibly();
            }

            @Override
            public boolean tryLock() {
                return lock.tryLock();
            }

            @Override
            public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
                return lock.tryLock(time, unit);
            }

            @Override
            public void unlock() {
                lock.unlock();
            }
        };
    }
}
