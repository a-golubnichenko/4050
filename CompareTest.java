package com.bigbrassband.jira.git;

import com.atlassian.jira.cluster.ClusterManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.bigbrassband.jira.git.ao.dao.ObjectRegistrationDao;
import com.bigbrassband.jira.git.ao.dao.UntrackedCommitNotificationDao;
import com.bigbrassband.jira.git.jiraservices.cluster.event.EventServiceFactory;
import com.bigbrassband.jira.git.services.gitmanager.SingleGitManager;
import com.bigbrassband.jira.git.services.gitviewer.compare.CompareCommitsGitAction;
import com.bigbrassband.jira.git.services.globalsettings.MockGlobalSettingsManager;
import com.bigbrassband.jira.git.services.indexer.files.FilesInfoIndexManager;
import com.bigbrassband.jira.git.services.indexer.pullrequests.MergeRequestIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.GitPluginIndexManagerImpl;
import com.bigbrassband.jira.git.services.indexer.revisions.MockIndexWriter;
import com.bigbrassband.jira.git.services.indexer.revisions.MockRevisionsIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.ReindexProgressMonitor;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionInfo;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionsIndexManagerImpl;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManager;
import com.bigbrassband.jira.git.services.scripting.ScriptTriggersHive;
import com.bigbrassband.jira.git.utils.Clock;
import com.bigbrassband.jira.git.utils.FileUtil;
import junit.framework.Assert;
import org.apache.lucene.document.Document;
import org.junit.After;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by ababilo on 10/12/15.
 */
public class CompareTest extends LocalRepositoryTest {

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        MockIndexWriter.docs.clear();
        MockRevisionsIndexManager.resetCriteria();
    }

    static class ResultCaptor<T> implements Answer<T> {

        Map<String, T> result = new HashMap<>();

        @Override
        public T answer(InvocationOnMock invocationOnMock) throws Throwable {
            final Integer repoId = (Integer) invocationOnMock.getArguments()[0];
            final String commit = (String) invocationOnMock.getArguments()[1];
            MockRevisionsIndexManager.setSearchCriteria(new Predicate<Document>() {
                @Override
                public boolean test(Document document) {
                    return document.get(RevisionsIndexManagerImpl.FIELD_REVISIONNUMBER).equals(commit)
                            && document.get(RevisionsIndexManagerImpl.FIELD_REPOSITORY).equals(repoId.toString());
                }
            });
            result.put(commit, (T) invocationOnMock.callRealMethod());
            return result.get(commit);
        }
    }

    @Test
    public void commitBranches() throws Exception {
        GitPluginPermissionManager permissionManager = mock(GitPluginPermissionManager.class);
        doReturn(true).when(permissionManager).hasReadAccess(any(Issue.class), any(ApplicationUser.class));
        doReturn(true).when(permissionManager).hasProjectReadAccessByIssueKey(anyString(), any(ApplicationUser.class));
        RevisionsIndexManagerImpl indexManager = MockRevisionsIndexManager.get(manager, issueManager, permissionManager);
        GitPluginIndexManagerImpl gitPluginIndexManager = spy(new GitPluginIndexManagerImpl(mock(FilesInfoIndexManager.class),
                indexManager, mock(MergeRequestIndexManager.class), manager,
                null, null, null, null, new MockI18nManager(), null,
                mock(UntrackedCommitNotificationDao.class), MockGlobalSettingsManager.get(),
                mock(ObjectRegistrationDao.class), mock(ScriptTriggersHive.class),
                mock(EventServiceFactory.class), mock(Clock.class), mock(ClusterManager.class)));
        doReturn(Collections.emptyList()).when(gitPluginIndexManager).buildCommitProcessors(anyBoolean());

        ResultCaptor resultCaptor = new ResultCaptor();
        doAnswer(resultCaptor).when(gitPluginIndexManager).getLogEntryByRepoAndRevision(anyInt(), anyString(), any(ApplicationUser.class));

        File file = repository.getFile(LocalRepositoryManager.README_MD);
        repository.createBranch("second");
        FileUtil.appendLine(file, "initial");
        String commit1 = commit("TST-3 Initial file", file);

        FileUtil.removeLine(file, 0);
        FileUtil.insertLine(file, "initial2", 0);
        String commit2 = commit("Changed file", file);

        //create index for commit1
        gitPluginIndexManager.updateIndex(manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID), new ReindexProgressMonitor());

        CompareCommitsGitAction compareCommitsGitAction = spy(new CompareCommitsGitAction(null, manager,
                gitPluginIndexManager, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null, null, null, null));
        doReturn(null).when(compareCommitsGitAction).getLoggedInUser();
        compareCommitsGitAction.setRepoId(DEFAULT_REPO_ID);
        SingleGitManager gitManager = manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID);
        compareCommitsGitAction.setSingleGitManager(gitManager);
        doReturn(gitManager.getId()).when(compareCommitsGitAction).getRepoId();
        List<RevisionInfo> revisions = compareCommitsGitAction.getRevisions(repository.getCommitObject(
                repository.getInitialCommit()), repository.getCommitObject(commit2));
        Assert.assertEquals(2, revisions.size());
        Assert.assertNull(resultCaptor.result.get(commit2));
        Assert.assertNotNull(resultCaptor.result.get(commit1));
    }
}
