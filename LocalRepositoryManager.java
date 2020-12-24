package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.services.indexer.revisions.NoteInfo;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddNoteCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.notes.Note;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by ababilo on 8/27/15.
 */
public class LocalRepositoryManager implements AutoCloseable {

    public static final String README_MD = "README.MD";
    private Repository repository;
    private Git api;
    private String initialCommit;
    private String root;
    private boolean closeRepo;

    public LocalRepositoryManager(String root, boolean createNew, boolean doInitialCommit) throws Exception {
        this.root = root;
        File folder = new File(root);
        if (createNew) {
            FileUtils.deleteDirectory(folder);
            InitCommand initCommand = Git.init();
            initCommand.setBare(false);
            initCommand.setDirectory(folder);
            api = initCommand.call();
            repository = api.getRepository();
            closeRepo = false;
            if (doInitialCommit) {
                addEmptyFile(README_MD);
                initialCommit = commit("Initial commit", new PersonIdent("author", "author@example.com"), null, false);
            }
        } else {
            repository = FileRepositoryBuilder.create(new File(root, ".git"));
            api = new Git(repository);
            closeRepo = true;
        }
    }

    @Override
    public void close() throws IOException {
        api.close();
        if (closeRepo) {
            repository.close();
        }
        FileUtils.deleteDirectory(new File(root));
    }

    public LocalRepositoryManager(String root) throws Exception {
        this(root, false, false);
    }

    public String getRoot() {
        return repository.getDirectory().getAbsolutePath();
    }

    public String getInitialCommit() {
        return initialCommit;
    }

    public void createTag(String name) throws GitAPIException {
        TagCommand createTagCommand = api.tag();
        createTagCommand.setName(name);
        createTagCommand.setMessage("This is a tag " + name);
        createTagCommand.call();
    }

    public void createBranch(String name) throws GitAPIException {
        CheckoutCommand createBranchCommand = api.checkout().setCreateBranch(true);
        createBranchCommand.setName(name);
        createBranchCommand.call();
    }

    public void deleteBranch(String name) throws GitAPIException {
        api.branchDelete().setForce(true).setBranchNames(name).call();
    }

    public void deleteTag(String name) throws GitAPIException {
        api.tagDelete().setTags(name).call();
    }

    public void push(String name) throws GitAPIException {
        api.push().add(name).call();
    }

    public void push() throws GitAPIException {
        api.push().call();
    }

    public void pull(String from) throws GitAPIException {
        api.pull().setRemoteBranchName(from).call();
    }

    public String merge(String from) throws IOException, GitAPIException {
        return api.merge().include(repository.resolve(from)).setStrategy(MergeStrategy.RECURSIVE).call().getNewHead().name();
    }

    public String getCurrentBranch() throws IOException {
        return repository.getBranch();
    }

    public void checkoutBranch(String branch) throws GitAPIException {
        api.checkout().setName(branch).call();
    }

    public NoteInfo addNote(String commit, String note, String namespace) throws IOException, GitAPIException {
        AddNoteCommand addNoteCommand = api.notesAdd().setMessage(note).setNotesRef(namespace);
        addNoteCommand.setObjectId(getRevCommitOfObjectId(repository.resolve(commit)));
        Note result = addNoteCommand.call();
        return new NoteInfo(commit, result.getData().name(), extractNoteContent(result), namespace);
    }

    public Collection<NoteInfo> listNotes(final String namespace) throws GitAPIException {
        List<Note> notes = api.notesList().setNotesRef(namespace).call();
        return notes.stream()
                .map(note -> {
                    try {
                        return new NoteInfo(note.name(), note.getData().name(), extractNoteContent(note), namespace);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    private String extractNoteContent(Note note) throws IOException {
        ObjectLoader loader = repository.open(note.getData());
        return new String(loader.getBytes(), StandardCharsets.UTF_8);
    }

    private RevCommit getRevCommitOfObjectId(ObjectId objectId) throws IOException {
        try (RevWalk revWalk = new RevWalk(repository)) {
            return revWalk.parseCommit(objectId);
        }
    }

    public RevCommit getCommitObject(String revision) throws IOException {
        return getRevCommitOfObjectId(repository.resolve(revision));
    }

    public String commit(String message, PersonIdent author, PersonIdent committer, boolean amend) throws GitAPIException {
        CommitCommand commitCommand = api.commit();
        commitCommand.setAmend(amend);
        if (null != author) {
            commitCommand.setAuthor(author);
        }
        if (null != committer) {
            commitCommand.setCommitter(committer);
        }
        commitCommand.setMessage(message);
        return commitCommand.call().name();
    }

    public void removeCommit(String commit) throws IOException, GitAPIException {
        api.revert().include(repository.resolve(commit)).call();
    }

    public File addEmptyFile(String fileName) throws IOException, GitAPIException {
        File myfile = new File(repository.getDirectory().getParent(), fileName);
        myfile.createNewFile();
        addFile(myfile);
        return myfile;
    }

    public File getFile(String fileName) {
        return new File(repository.getDirectory().getParent(), fileName);
    }

    public void addFile(File file) throws GitAPIException {
        api.add().addFilepattern(file.getName()).call();
    }

    public void removeFile(File file) throws GitAPIException {
        api.rm().addFilepattern(file.getName()).call();
    }

    public String executeCommand(String[] command) throws InterruptedException, IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(new File(getRoot()).getAbsoluteFile());
        builder.redirectErrorStream(true);
        Process process = builder.start();

        Scanner s = new Scanner(process.getInputStream());
        StringBuilder text = new StringBuilder();
        while (s.hasNextLine()) {
            text.append(s.nextLine());
            text.append("\n");
        }
        s.close();

        int result = process.waitFor();
        return text.toString();
    }

    public String addTag(String tagName) throws GitAPIException {
        return api.tag().setName(tagName).call().getObjectId().name();
    }
}
