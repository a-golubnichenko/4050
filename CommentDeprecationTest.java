package com.bigbrassband.jira.git;

import com.bigbrassband.common.git.diff.bean.SourceFile;
import com.bigbrassband.jira.git.services.comments.CommentData;
import com.bigbrassband.jira.git.services.gitmanager.SingleGitManager;
import com.bigbrassband.jira.git.services.issuetabpanels.ChangesHelper;
import com.bigbrassband.jira.git.utils.FileUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ababilo on 8/27/15.
 */
@RunWith(JUnit4.class)
public class CommentDeprecationTest extends LocalRepositoryTest {

    private ChangesHelper changesHelper;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        changesHelper = new ChangesHelper(manager, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    private Collection<CommentData> doFilter(List<CommentData> comments, String baseRevision, String currentRevision, String path) throws Exception {
        Map<String, List<Pair<Long, Long>>> filesRange = new HashMap<>();
        List<SourceFile> sourceFiles = manager.<SingleGitManager>getGitManager(DEFAULT_REPO_ID).getDiffsForRevisionsAndFile(baseRevision, currentRevision, path).getDiff();
        changesHelper.collectRange(sourceFiles, filesRange);

        return changesHelper.excludeOutdated(comments, currentRevision, filesRange);
    }

    @Test
    public void commentAdded() throws Exception {
        // add line and comment
        repository.createBranch("second");
        File file = repository.addEmptyFile("file");
        FileUtil.appendLine(file, "new line");
        String commit = commit("Added file", file);

        List<CommentData> comments = new ArrayList<>();
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commit, 0L, 1L, null, null));
        // 1 comment should be present
        Assert.assertEquals(1, doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()).size());
        // add another line after
        FileUtil.appendLine(file, "second line");
        commit = commit("Modified file", file);
        // comment shouldn't disappear
        Assert.assertEquals(1, doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()).size());
    }

    @Test
    public void commentRemoved() throws Exception {
        // add 2 lines and comment the second line
        repository.createBranch("second");
        File file = repository.addEmptyFile("file");
        FileUtil.appendLine(file, "new line");
        FileUtil.appendLine(file, "second line");
        String commit = commit("Added file", file);

        List<CommentData> comments = new ArrayList<>();
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commit, 0L, 2L, null, null));
        // 1 comment should be present
        Assert.assertEquals(1, doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()).size());
        // remove second line
        FileUtil.removeLine(file, 1);
        commit = commit("Modified file", file);
        // comment shouldn't appear
        Assert.assertEquals(0, doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()).size());
    }

    @Test
    public void commentAddedBefore() throws Exception {
        // add line and comment
        repository.createBranch("second");
        File file = repository.addEmptyFile("file");
        FileUtil.appendLine(file, "new line");
        String commentCommit = commit("Added file", file);

        List<CommentData> comments = new ArrayList<>();
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commentCommit, 0L, 1L, null, null));
        // 1 comment should be present
        Assert.assertEquals(1, doFilter(comments, this.repository.getInitialCommit(), commentCommit, file.getName()).size());
        // insert another line before first
        FileUtil.insertLine(file, "new one line", 0);
        String commit = commit("Modified file", file);
        List<CommentData> outdated = new ArrayList<>(doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()));
        CommentData comment = outdated.iterator().next();
        // comment should be present and moved down to second line
        Assert.assertEquals(0L, comment.getOldLineNumber().longValue());
        Assert.assertEquals(2L, comment.getNewLineNumber().longValue());
        Assert.assertEquals(1, outdated.size());
        comments = new ArrayList<>();
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commentCommit, 0L, 1L, null, null));
        // remove first line
        FileUtil.removeLine(file, 0);
        commit = commit("Modified file2", file);
        List<CommentData> outdated2 = new ArrayList<>(doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()));
        comment = outdated2.iterator().next();
        // comment should be present and moved back to first line
        Assert.assertEquals(0L, comment.getOldLineNumber().longValue());
        Assert.assertEquals(1L, comment.getNewLineNumber().longValue());
        Assert.assertEquals(1, outdated.size());
    }

    @Test
    public void commentRemovedBefore() throws Exception {
        // add 2 lines and comment the second
        repository.createBranch("second");
        File file = repository.addEmptyFile("file");
        FileUtil.appendLine(file, "new line");
        FileUtil.appendLine(file, "new one line");
        String commentCommit = commit("Added file", file);

        List<CommentData> comments = new ArrayList<>();
        // 1 comment should be present
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commentCommit, 0L, 2L, null, null));
        Assert.assertEquals(1, doFilter(comments, this.repository.getInitialCommit(), commentCommit, file.getName()).size());
        // remove first line
        FileUtil.removeLine(file, 0);
        String commit = commit("Modified file", file);
        List<CommentData> outdated = new ArrayList<>(doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()));
        CommentData comment = outdated.iterator().next();
        // comment should be present and moved up to first line
        Assert.assertEquals(0L, comment.getOldLineNumber().longValue());
        Assert.assertEquals(1L, comment.getNewLineNumber().longValue());
        Assert.assertEquals(1, outdated.size());
        comments = new ArrayList<>();
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commentCommit, 0L, 2L, null, null));
        // insert back first line
        FileUtil.insertLine(file, "new line", 0);
        commit = commit("Modified file2", file);
        List<CommentData> outdated2 = new ArrayList<>(doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()));
        comment = outdated2.iterator().next();
        // comment should be present and moved back to second line
        Assert.assertEquals(0L, comment.getOldLineNumber().longValue());
        Assert.assertEquals(2L, comment.getNewLineNumber().longValue());
        Assert.assertEquals(1, outdated.size());
    }

    @Test
    public void commentRemoveInThird() throws Exception {
        // add 7 lines and comment the second
        repository.createBranch("second");
        File file = repository.addEmptyFile("file");
        for (int i = 0; i < 7; i++) {
            FileUtil.appendLine(file, "new line");
        }
        String commentCommit = commit("Added file", file);

        List<CommentData> comments = new ArrayList<>();
        // 1 comment should be present
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commentCommit, 0L, 2L, null, null));
        Assert.assertEquals(1, doFilter(comments, this.repository.getInitialCommit(), commentCommit, file.getName()).size());
        // add some lines to begin
        FileUtil.insertLine(file, "new line", 0);
        FileUtil.insertLine(file, "new line2", 0);
        String commit = commit("Modified file", file);
        List<CommentData> outdated = new ArrayList<>(doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()));
        CommentData comment = outdated.iterator().next();
        // comment should be present and moved down
        Assert.assertEquals(0L, comment.getOldLineNumber().longValue());
        Assert.assertEquals(4L, comment.getNewLineNumber().longValue());
        Assert.assertEquals(1, outdated.size());
        // remove some lines at end
        FileUtil.removeLine(file, 6);
        FileUtil.removeLine(file, 7);
        commit = commit("Modified file", file);
        outdated = new ArrayList<>(doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()));
        comment = outdated.iterator().next();
        // comment should be present and not changed
        Assert.assertEquals(0L, comment.getOldLineNumber().longValue());
        Assert.assertEquals(4L, comment.getNewLineNumber().longValue());
        Assert.assertEquals(1, outdated.size());
        comments = new ArrayList<>();
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commentCommit, 0L, 2L, null, null));
        // remove start lines back
        FileUtil.removeLine(file, 0);
        FileUtil.removeLine(file, 0);
        commit = commit("Modified file2", file);
        List<CommentData> outdated2 = new ArrayList<>(doFilter(comments, this.repository.getInitialCommit(), commit, file.getName()));
        comment = outdated2.iterator().next();
        // comment should be present and moved back to second line
        Assert.assertEquals(0L, comment.getOldLineNumber().longValue());
        Assert.assertEquals(2L, comment.getNewLineNumber().longValue());
        Assert.assertEquals(1, outdated.size());
    }

    @Test// see GIT-1060 bug 2.2
    public void bug22() throws Exception {
        // add initial commit
        repository.createBranch("second");
        File file = repository.addEmptyFile("file");
        for (int i = 0; i < 7; i++) {
            FileUtil.appendLine(file, "new line" + i);
        }
        String initialCommit = commit("Added file", file);

        // append line to the endff
        FileUtil.appendLine(file, "last line");
        String commentCommit = commit("Modified file", file);

        List<CommentData> comments = new ArrayList<>();
        // comment last grey line
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commentCommit, 7L, 7L, null, null));
        Assert.assertEquals(1, doFilter(comments, initialCommit, commentCommit, file.getName()).size());

        // add some lines to begin
        FileUtil.insertLine(file, "new line", 0);
        FileUtil.insertLine(file, "new line11", 0);
        // change line
        FileUtil.removeLine(file, 8);
        FileUtil.insertLine(file, "another line", 8);
        String commit = commit("Modified file", file);
        // comment should disappear
        List<CommentData> outdated = new ArrayList<>(doFilter(comments, initialCommit, commit, file.getName()));
        Assert.assertEquals(0, outdated.size());
    }

    @Test// see GIT-1060 bug 4
    public void bug4() throws Exception {
        // add initial commit
        repository.createBranch("second");
        File file = repository.addEmptyFile("file");
        for (int i = 0; i < 7; i++) {
            FileUtil.appendLine(file, "new line" + i);
        }
        String initialCommit = commit("Added file", file);

        // add some lines to begin
        FileUtil.insertLine(file, "new line", 2);
        FileUtil.insertLine(file, "new line11", 3);
        String commentCommit = commit("Modified file", file);

        List<CommentData> comments = new ArrayList<>();
        // comment green line and grey line with the same old as its new, and line below
        comments.add(new CommentData(1, "comment1", null, null, DEFAULT_REPO_ID, "file", commentCommit, 0L, 3L, null, null));
        comments.add(new CommentData(2, "comment2", null, null, DEFAULT_REPO_ID, "file", commentCommit, 3L, 5L, null, null));
        comments.add(new CommentData(3, "comment3", null, null, DEFAULT_REPO_ID, "file", commentCommit, 5L, 7L, null, null));
        Assert.assertEquals(3, doFilter(comments, initialCommit, commentCommit, file.getName()).size());

        // delete grey line
        FileUtil.removeLine(file, 4);
        String commit = commit("Modified file", file);
        // comment2 should disappear, both other should be present
        List<CommentData> outdated = new ArrayList<>(doFilter(comments, initialCommit, commit, file.getName()));
        Assert.assertEquals(2, outdated.size());
    }

    @Test
    public void bug5() throws Exception {
        // add initial commit
        repository.createBranch("second");
        File file = repository.addEmptyFile("file");
        FileUtil.appendLine(file, "В лесу родилась елочка" + 0);
        FileUtil.appendLine(file, "В лесу она росла      " + 1);
        FileUtil.appendLine(file, "Зимой и летом стройная" + 2);
        FileUtil.appendLine(file, "Зеленая была          " + 3);
        FileUtil.appendLine(file, "--------1-------------" + 4);
        FileUtil.appendLine(file, "Метель ей пела песенку" + 5);
        FileUtil.appendLine(file, "Спи елочка бай-бай    " + 6);
        FileUtil.appendLine(file, "Мороз снежком укутывал" + 7);
        FileUtil.appendLine(file, "Смотри не замерзай    " + 8);
        FileUtil.appendLine(file, "--------2-------------" + 9);

        String initialCommit = commit("Added file", file);

        // remove line "Спи елочка бай-бай"
        FileUtil.removeLine(file, 6);
        String commit1 = commit("Modified file", file);

        // add comment for "--------2-------------"
        List<CommentData> comments = new ArrayList<>();
        comments.add(new CommentData(1, "comment", null, null, DEFAULT_REPO_ID, "file", commit1, 10L, 9L, null, null));
        // 1 comment should be present
        Assert.assertEquals(1, doFilter(comments, initialCommit, commit1, file.getName()).size());

        // remove "Смотри не замерзай" and add a new line after "Зеленая была"
        FileUtil.removeLine(file, 8 - 1);
        FileUtil.insertLine(file, "new line", 4);
        String commit2 = commit("Modified file", file);

        // 1 comment should be present
        Assert.assertEquals(1, doFilter(comments, initialCommit, commit2, file.getName()).size());
    }

    @Test //GIT-2122
    public void bugGIT2122() throws Exception {
        // add initial commit
        repository.createBranch("second");
        File file = repository.addEmptyFile("file");
        FileUtil.appendLine(file, "В лесу родилась елочка," + 1);
        FileUtil.appendLine(file, "В лесу она росла.      " + 2);
        FileUtil.appendLine(file, "Зимой и летом стройная," + 3);
        FileUtil.appendLine(file, "Зеленая была.          " + 4);
        FileUtil.appendLine(file, "--------1-------------" + 5);
        FileUtil.appendLine(file, "Метель ей пела песенку:" + 6);
        FileUtil.appendLine(file, "Спи елочка бай-бай!    " + 7);
        FileUtil.appendLine(file, "Мороз снежком укутывал:" + 8);
        FileUtil.appendLine(file, "Смотри не замерзай!    " + 9);
        FileUtil.appendLine(file, "--------2-------------" + 10);
        FileUtil.appendLine(file, "Трусишка зайка серенький  " + 11);
        FileUtil.appendLine(file, "Под ёлочкой скакал.       " + 12);
        FileUtil.appendLine(file, "Порою волк, сердитый волк," + 13);
        FileUtil.appendLine(file, "Рысцою пробегал.          " + 14);

        String initialCommit = commit("Added file", file);

        // edit line "Порою волк, сердитый волк"
        FileUtil.removeLine(file, 12);
        FileUtil.insertLine(file, "changed line", 12);
        String commit1 = commit("Modified file", file);

        // add comment for "changed line"
        List<CommentData> comments = new ArrayList<>();
        comments.add(new CommentData(1, "change the line in a different way", null, null, DEFAULT_REPO_ID, "file", commit1, 0L, 13L, null, null));
        // 1 comment should be present
        Assert.assertEquals(1, doFilter(comments, initialCommit, commit1, file.getName()).size());

        // change "changed line" as it was asked
        FileUtil.removeLine(file, 12);
        FileUtil.insertLine(file, "changed line - 2", 12);

        // remove a line at the begin
        FileUtil.removeLine(file, 0);

        // add several lines in the middle
        FileUtil.insertLine(file, "added line", 2);
        FileUtil.insertLine(file, "added line", 2);
        FileUtil.insertLine(file, "added line", 2);
        FileUtil.insertLine(file, "added line", 2);

        String commit2 = commit("Modified file", file);

        // 0 comment should be present
        Assert.assertEquals(0, doFilter(comments, initialCommit, commit2, file.getName()).size());
    }
}
