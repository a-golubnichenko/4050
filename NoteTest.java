package com.bigbrassband.jira.git;

import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.bigbrassband.jira.git.services.gitmanager.SingleGitManager;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManager;
import com.bigbrassband.jira.git.services.indexer.revisions.CommitProcessor;
import com.bigbrassband.jira.git.services.indexer.revisions.MockIndexWriter;
import com.bigbrassband.jira.git.services.indexer.revisions.MockRevisionsIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.NoteInfo;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionsIndexManager;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionsIndexManagerImpl;
import org.apache.lucene.document.Document;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ababilo on 8/28/15.
 */
@RunWith(JUnit4.class)
public class NoteTest extends LocalRepositoryTest {

    private RevisionsIndexManager indexManager;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ApplicationUser currentUser = new MockApplicationUser("admin", "admin", "admin@example.com");
        GitPluginPermissionManager permissionManager = MockPermissionManager.allPermissionsManager(currentUser);
        indexManager = MockRevisionsIndexManager.get(manager, issueManager, permissionManager);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        MockRevisionsIndexManager.resetCriteria();
        MockIndexWriter.docs.clear();
    }

    private void updateNotesIndex(int repoId, NoteInfo noteInfo) throws IndexException, IOException {
        MockRevisionsIndexManager.setSearchCriteria(new NoteCriteria(repoId, noteInfo));
        indexManager.updateNotesIndex(manager.<SingleGitManager>getGitManager(repoId).getNotes(), manager.<SingleGitManager>getGitManager(repoId));
    }

    @Test
    public void existentNoteWithKey() throws Exception {
        repository.createBranch("second");
        repository.addEmptyFile("file");
        String commit = commit("Added file");
        NoteInfo note = repository.addNote(commit, "[TST-3] Note", "refs/notes/jira");

        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit), manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID),
                Collections.<CommitProcessor>emptyList(), false);
        Collection<NoteInfo> notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(note, notes.iterator().next());
    }

    @Test
    public void existentCommitWithKey() throws Exception {
        repository.createBranch("second");
        repository.addEmptyFile("file");
        String commit = commit("[TST-3] Added file");
        NoteInfo note = repository.addNote(commit, "Note", "refs/notes/jira");

        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit), manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID),
                Collections.<CommitProcessor>emptyList(), false);
        Collection<NoteInfo> notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(note, notes.iterator().next());
    }

    @Test
    public void existentDifferentKeys() throws Exception {
        repository.createBranch("second");
        repository.addEmptyFile("file");
        String commit = commit("[TST-2] Added file");
        NoteInfo note = repository.addNote(commit, "[TST-3] Note", "refs/notes/jira");

        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit), manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID),
                Collections.<CommitProcessor>emptyList(), false);
        Collection<NoteInfo> notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(note, notes.iterator().next());
    }

    @Test//GIT-1314,GIT-1324
    public void alreadyIndexedCommit() throws Exception {
        repository.createBranch("second");
        repository.addEmptyFile("file");
        String commit = commit("[TST-2] Added file");

        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit),
                manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID), Collections.<CommitProcessor>emptyList(), false);
        Collection<NoteInfo> notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(1, MockIndexWriter.docs.size());
        Assert.assertEquals(0, notes.size());

        NoteInfo note = repository.addNote(commit, "[TST-3] Note", "refs/notes/jira");
        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit),
                manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID), Collections.<CommitProcessor>emptyList(), false);
        notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(1, MockIndexWriter.docs.size());
        Assert.assertEquals(0, notes.size());

        updateNotesIndex(DEFAULT_REPO_ID, note);
        notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(1, MockIndexWriter.docs.size());
        Assert.assertEquals(note, notes.iterator().next());

        List<Document> docs = indexManager.getByRepoAndRevision(DEFAULT_REPO_ID, commit);
        Assert.assertArrayEquals(new String[]{"TST-2", "TST-3"}, docs.get(0).getValues(RevisionsIndexManagerImpl.FIELD_ISSUEKEY));
    }

    @Test//GIT-1324
    public void alreadyIndexedCommitNoIssueKey() throws Exception {
        repository.createBranch("second");
        repository.addEmptyFile("file");
        String commit = commit("Added file");

        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit), manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID),
                Collections.<CommitProcessor>emptyList(), false);
        Collection<NoteInfo> notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(0, MockIndexWriter.docs.size());
        Assert.assertEquals(0, notes.size());

        NoteInfo note = repository.addNote(commit, "[TST-3] Note", "refs/notes/jira");
        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit), manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID),
                Collections.<CommitProcessor>emptyList(), false);
        notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(0, MockIndexWriter.docs.size());
        Assert.assertEquals(0, notes.size());

        updateNotesIndex(DEFAULT_REPO_ID, note);
        notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(1, MockIndexWriter.docs.size());
        Assert.assertEquals(note, notes.iterator().next());

        List<Document> docs = indexManager.getByRepoAndRevision(DEFAULT_REPO_ID, commit);
        Assert.assertArrayEquals(new String[]{"TST-3"}, docs.get(0).getValues(RevisionsIndexManagerImpl.FIELD_ISSUEKEY));
    }

    @Test//GIT-1314
    public void noIssueKey() throws Exception {
        repository.createBranch("second");
        repository.addEmptyFile("file");
        String commit = commit("Added file");

        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit),
                manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID), Collections.<CommitProcessor>emptyList(), false);
        Collection<NoteInfo> notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(0, MockIndexWriter.docs.size());
        Assert.assertEquals(0, notes.size());

        NoteInfo note = repository.addNote(commit, "Note", "refs/notes/jira");
        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit),
                manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID), Collections.<CommitProcessor>emptyList(), false);
        notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(0, MockIndexWriter.docs.size());
        Assert.assertEquals(0, notes.size());

        updateNotesIndex(DEFAULT_REPO_ID, note);
        notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(0, MockIndexWriter.docs.size());
        Assert.assertEquals(0, notes.size());
    }

    @Test
    public void unexistentCommit() throws Exception {
        repository.createBranch("second");
        repository.addEmptyFile("file");
        String commit = commit("Added file");
        NoteInfo note = repository.addNote(commit, "[TST-3] Note", "refs/notes/jira");
        repository.removeCommit(commit);

        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit), manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID),
                Collections.<CommitProcessor>emptyList(), false);
        Collection<NoteInfo> notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(note, notes.iterator().next());
    }

    @Test
    public void ordering() throws Exception {
        repository.createBranch("second");
        repository.addEmptyFile("file");
        String commit = commit("Added file");
        repository.addNote(commit, "[TST-3] Note1", "refs/notes/jira");
        repository.addNote(commit, "[TST-3] Note2", "refs/notes/aaa");
        repository.addNote(commit, "[TST-3] Note3", "refs/notes/commits");

        indexManager.updateBranchIndex(DEFAULT_REPO_ID, "second", Collections.singletonMap("second", commit), manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID),
                Collections.<CommitProcessor>emptyList(), false);
        List<NoteInfo> notes = indexManager.getNotes(DEFAULT_REPO_ID, commit);
        Assert.assertEquals(3, notes.size());
        Assert.assertEquals("refs/notes/aaa", notes.get(0).getNamespace());
        Assert.assertEquals("refs/notes/commits", notes.get(1).getNamespace());
        Assert.assertEquals("refs/notes/jira", notes.get(2).getNamespace());
    }
}
