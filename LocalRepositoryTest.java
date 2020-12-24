package com.bigbrassband.jira.git;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.bigbrassband.jira.git.services.props.GProperties;
import com.bigbrassband.jira.git.services.props.GitProperties;
import com.bigbrassband.jira.git.services.weblinks.WebLinkDao;
import com.bigbrassband.jira.git.utils.JiraUtils;
import com.bigbrassband.jira.git.utils.MockCronExpressionValidator;
import com.google.common.collect.ImmutableMap;
import com.bigbrassband.jira.git.ao.MockGitRepositoryDao;
import com.bigbrassband.jira.git.ao.dao.RepositoryWatcherDao;
import com.bigbrassband.jira.git.services.gitmanager.MockMultipleGitManager;
import com.bigbrassband.jira.git.services.gitmanager.SingleGitManager;
import com.bigbrassband.jira.git.services.globalsettings.GlobalSettingsManager;
import com.bigbrassband.jira.git.services.globalsettings.MockGlobalSettingsManager;
import com.bigbrassband.jira.git.services.integration.ExternalServiceFactory;
import com.bigbrassband.jira.git.services.integration.ExternalServiceFactoryImpl;
import com.bigbrassband.jira.git.services.integration.IntegrationPropertiesHolderImpl;
import com.bigbrassband.jira.git.services.integration.IntegrationType;
import com.bigbrassband.jira.git.services.indexer.revisions.NoteInfo;
import com.bigbrassband.jira.git.services.indexer.revisions.RevisionsIndexManagerImpl;
import com.bigbrassband.jira.git.utils.JGitUtils;
import com.bigbrassband.jira.git.utils.ValidationUtil;
import com.bigbrassband.jira.git.services.weblinks.WebLinkTemplateManagerImpl;
import com.bigbrassband.common.git.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.lucene.document.Document;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.function.Predicate;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

/**
 * Created by ababilo on 8/31/15.
 */
public class LocalRepositoryTest {

    public final static String FILE_SEPARATOR = System.getProperty("file.separator");
    protected static final int DEFAULT_REPO_ID = 1;
    protected static final int ANOTHER_REPO_ID = 2;
    private static final String PROJECT_KEY = "TST";

    public static class NoteCriteria implements Predicate<Document> {

        private int repoId;
        private NoteInfo noteInfo;

        public NoteCriteria(int repoId, NoteInfo noteInfo) {
            this.repoId = repoId;
            this.noteInfo = noteInfo;
        }

        @Override
        public boolean test(Document doc) {
            return String.valueOf(repoId).equals(doc.get(RevisionsIndexManagerImpl.FIELD_REPOSITORY))
                    && noteInfo.toString().equals(doc.get(RevisionsIndexManagerImpl.FIELD_NOTE));
        }
    }

    protected MockMultipleGitManager manager;
    protected LocalRepositoryManager repository;
    protected MockGitRepositoryDao dao;
    protected WebLinkDao webLinkDao;
    protected RepositoryWatcherDao repoWatcherDao;
    protected ApplicationUser currentUser;
    protected SingleGitManager singleGitManager;
    protected ExternalServiceFactory externalServiceFactory;
    protected IssueManager issueManager;
    protected JiraUtils jiraUtils;
    protected String tmpPath;

    @BeforeClass
    public static void beforeClass() throws Exception {
        MockApplicationProperties.putDefaultMockToComponentAccessor(ImmutableMap.<String, Object>of("jira.projectkey.pattern", "")); // fall back to default pattern
        MockUserLocaleStore.putDefaultMockToComponentAccessor(Locale.getDefault());
        MockAuthenticationContextInitializer.putDefaultMockToComponentAccessor();
        Util.disableAutoGcOnFetch();
        JGitUtils.setupJGitWindowCache(GlobalSettingsManager.DEFAULT_MAX_OPEN_PACK_FILES_COUNT);
    }

    protected WebLinkDao createViewLinkFormatDao() {
        return mock(WebLinkDao.class);
    }

    protected ExternalServiceFactory createExternalServiceFactory() throws Exception {
        return new ExternalServiceFactoryImpl(new WebLinkTemplateManagerImpl(),
                new IntegrationPropertiesHolderImpl(), MockGlobalSettingsManager.get(),
                new ValidationUtil(new MockI18nHelper(), null, jiraUtils, null, new MockCronExpressionValidator()), new MockI18nHelper());
    }

    protected ImmutablePair<LocalRepositoryManager, SingleGitManager> setupRepository(int repoId) throws Exception {
        return setupRepository(repoId, null, true, true);
    }

    protected ImmutablePair<LocalRepositoryManager, SingleGitManager> setupRepository(
            int repoId, Boolean enableSmartCommits, boolean revisionIndexing, boolean isGlobal
    ) throws Exception {
        String root = String.format(tmpPath + "/repositories/%s/repository/", RandomStringUtils.randomAlphanumeric(13));
        LocalRepositoryManager repo = new LocalRepositoryManager(root, true, true);

        GProperties props = new MockGProperties(repoId);
        props.setRootToAbsolutePath(new File(root).getAbsolutePath());
        props.setRevisionIndexing(revisionIndexing);
        props.setEnableFetches(false);
        props.setRepositoryKey(Integer.toString(repoId));
        props.setHosted(true);
        props.setGlobal(isGlobal);
        if (enableSmartCommits != null) {
            props.setSmartCommitsEnabled(enableSmartCommits);
        }
        SingleGitManager singleGitManager = (SingleGitManager)manager.createRepository(props, Collections.emptyList());

        return new ImmutablePair<>(repo, singleGitManager);
    }

    @Before
    public void setUp() throws Exception {
        tmpPath = SystemUtils.getJavaIoTmpDir().getAbsolutePath();
        jiraUtils = new MockJiraUtilsWithCodeReviewEnabled();
        dao = Mockito.spy(new MockGitRepositoryDao(jiraUtils));
        webLinkDao = createViewLinkFormatDao();
        repoWatcherDao = Mockito.spy(new MockRepositoryWatcherDaoImpl());
        externalServiceFactory = createExternalServiceFactory();
        manager = MockMultipleGitManager.get(dao, webLinkDao, jiraUtils, repoWatcherDao, externalServiceFactory);
        ImmutablePair<LocalRepositoryManager, SingleGitManager> repoData = setupRepository(DEFAULT_REPO_ID);
        repository = repoData.left;
        singleGitManager = repoData.right;
        currentUser = new MockApplicationUser("admin", "admin", "admin@example.com");
        issueManager = mock(IssueManager.class);

        Mockito.when(issueManager.getIssueObject(anyString())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                return createMockIssue((String)args[0]);
            }
        });
    }

    protected boolean isRevisionIndexing() {
        return true;
    }

    @After
    public void tearDown() throws Exception {
        repository.close();
        manager.close();
        FileUtils.deleteDirectory(new File(tmpPath + "/repositories/"));
        FileUtils.deleteDirectory(new File(tmpPath + "/data/git-plugin/"));
    }

    protected String commit(String message) throws GitAPIException {
        return commit(repository, message);
    }

    protected String commit(String message, File... files) throws GitAPIException {
        return commit(repository, message, files);
    }

    protected String commit(LocalRepositoryManager repository, String message) throws GitAPIException {
        return repository.commit(message, new PersonIdent("author", "author@example.com"),
                new PersonIdent("committer", "committer@example.com"), false);
    }

    protected String commit(LocalRepositoryManager repository, String message, Date when, TimeZone tz) throws GitAPIException {
        return repository.commit(message, new PersonIdent("author", "author@example.com", when, tz),
                new PersonIdent("committer", "committer@example.com", when, tz), false);
    }

    protected void branch(LocalRepositoryManager repository, String name) throws GitAPIException {
        repository.createBranch(name);
    }

    protected void checkout(LocalRepositoryManager repository, String name) throws GitAPIException {
        repository.checkoutBranch(name);
    }

    protected String commit(LocalRepositoryManager repository, String message, File... files) throws GitAPIException {
        for (File file : files) {
            repository.addFile(file);
        }
        return commit(repository, message);
    }

    protected SingleGitManager cloneFrom(int repoId, String origin) throws Exception {
        return cloneFrom(repoId, origin, false);
    }

    protected SingleGitManager cloneFrom(int repoId, String origin, boolean isBare) throws Exception {
        String tmp = SystemUtils.getJavaIoTmpDir().getAbsolutePath();
        String path = String.format(tmp + "/repositories/%s/repository/", RandomStringUtils.randomAlphanumeric(13));
        File root = new File(path);
        root.mkdirs();
        root.deleteOnExit();
        FileUtils.copyDirectory(new File(origin), root);

        //String rootPath = path;
        if (isBare) {
            for (File file: root.listFiles()) {
                if(file.getName().equals(".git")) {
                    //do nothing
                } else {
                    //delete file
                    file.delete();
                }
            }
            File gitDir = new File(path + ".git");
            gitDir.renameTo(new File(path + "repository.git"));
            path = path + "repository.git";
        }

        GProperties props = new MockGProperties(repoId);
        props.setRootToAbsolutePath(path);
        props.setOrigin(origin);
        props.setEnableFetches(true);
        props.setPassword("just a password");
        return manager.<SingleGitManager>createRepository(props, Collections.emptyList());
    }


    protected static GProperties createProps(IntegrationType type, String origin, String username, String password, boolean disableSslVerification) {
        GProperties props = new GitProperties();
        GProperties.Util.fillDefaults(props);
        props.setOrigin(origin);
        props.setUsername(username);
        props.setPassword(password);
        props.setDisableSslVerification(disableSslVerification);
        props.setIntegrationType(type);
        return props;
    }

    public static List<Issue> createMockIssues(Collection<String> issueKeys) {
        List<Issue> issues = new ArrayList<>();
        for(final String issueKey: issueKeys) {
            issues.add(createMockIssue(issueKey));
        }
        return issues;
    }

    public static Issue createMockIssue(String issueKey) {
        Random r = new Random();
        MockIssue issueOne = new MockIssue(1, issueKey);
        Project project = new MockProject(10000, PROJECT_KEY);
        issueOne.setProjectObject(project);
        issueOne.setId(r.nextLong());
        issueOne.setKey(issueKey);
        return issueOne;
    }
}
