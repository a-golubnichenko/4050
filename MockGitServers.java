package com.bigbrassband.jira.git;

import com.atlassian.core.util.FileUtils;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;
import it.util.RepositoryImporter;
import org.apache.log4j.Logger;
import org.junit.Assert;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * This component acts as a remote server configured with self-signed SSL cert
 * When started it handles several requests. Assume them as if they are a separate deployed services:
 * - Remote Git Server clonable via HTTPS. The repository is test repo: jiragit-test-data.zip
 * - GitLab CE Server accessible via HTTPS. Mocks /session and /project APIs (v3)
 *
 * See related issues for more details and main purposes: GIT-2255
 */
public class MockGitServers {

    private static Logger log = Logger.getLogger(MockGitServers.class);

    private static final int HTTPS_PORT = 8443;
    public static final String RESOURCES_REPOSITORIES_PATH_ZIPPED_PATH = "repositories/jiragit-test-data/jiragit-test-data.zip";

    public static final String GITLAB_ORIGIN_URL = String.format("https://localhost:%d/repo", HTTPS_PORT);
    public static final String REPOSITORY_DISPLAY_NAME = "jiragit-test-data";
    public static final String PROXIED_REPO_LOCAL_FOLDER = REPOSITORY_DISPLAY_NAME + ".git";
    public static final String REPOSITORY_ORIGIN_URL_HTTPS = String.format("https://localhost:%d/%s.git", HTTPS_PORT, REPOSITORY_DISPLAY_NAME);
    public static final String REPOSITORY_ORIGIN_URL_HTTP = String.format("http://localhost:%d/%s.git", HTTPS_PORT, REPOSITORY_DISPLAY_NAME);

    private static DeploymentManager manager;
    private static Undertow server;
    private static boolean managerUtilized, serverUtilized;

    public static void startServer() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        startServer(true);
    }

    public static void startServer(boolean sslEnabled) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        unzipRepository();

        SSLContext sc = SSLContext.getInstance("SSL");

        // SSLContext algorithms: SunX509
        KeyManagerFactory kmf
                = KeyManagerFactory.getInstance("SunX509");

        // KeyStore types: JKS
        char ksPass[] = "password".toCharArray();
        char ctPass[] = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(MockGitServers.class.getResource("/certs/keystore.jks").getPath()), ksPass);

        // Generating KeyManager list
        kmf.init(ks,ctPass);
        KeyManager[] kmList = kmf.getKeyManagers();

        DeploymentInfo servletBuilder = Servlets.deployment();

        ServletInfo gitServletInfo = new ServletInfo("git_servlet", MockGitServlet.class)
                .addMapping("/*")
                .setLoadOnStartup(1);
        ServletInfo gitLabServletInfo = new ServletInfo("gitlab_servlet", GitLabMockServlet.class)
                .addMapping("/repo/api/v3/*")
                .setLoadOnStartup(1);
        ServletInfo gitHubServletInfo = new ServletInfo("github_servlet", GitHubMockServlet.class)
                .addMapping("/repo/api/v3/user/repos/*")
                .setLoadOnStartup(1);
        ServletInfo gitHubServletInfo2 = new ServletInfo("github_servlet_2", GitHubMockServlet.class)
                .addMapping("/repo/api/v3/repos/*")
                .setLoadOnStartup(1);


        sc.init(kmList, null, new SecureRandom());

        servletBuilder
                .setClassLoader(MockGitServers.class.getClassLoader())
            .setContextPath("/")
            .addServlets(gitServletInfo, gitLabServletInfo, gitHubServletInfo, gitHubServletInfo2)
                .setDeploymentName("git_servlet");
        manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();

        try {
            Undertow.Builder builder = Undertow
                    .builder()
                    .setWorkerThreads(1);
            if (sslEnabled) {
                builder = builder.addHttpsListener(HTTPS_PORT, "0.0.0.0", sc);
            } else {
                builder = builder.addHttpListener(HTTPS_PORT, "0.0.0.0");
            }
            HttpHandler next = manager.start();
            builder.setHandler(next);

            server = builder.build();
            server.start();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
        managerUtilized = false;
        serverUtilized = false;
    }

    public static void dispose() {
        if (manager != null && !managerUtilized) {
            manager.undeploy();
            managerUtilized = true;
        }
        if (server != null && !serverUtilized) {
            server.stop();
            serverUtilized = true;
        }
        File gitDir = new File(PROXIED_REPO_LOCAL_FOLDER);
        if (gitDir.exists()) {
            log.info("Deleting repository folder: " + PROXIED_REPO_LOCAL_FOLDER);
            FileUtils.deleteDir(gitDir);
        }
    }

    private static void unzipRepository() {
        log.info(String.format("Adding local repository [%s] ..", REPOSITORY_DISPLAY_NAME));
        RepositoryImporter repositoryImporter = new RepositoryImporter(REPOSITORY_DISPLAY_NAME, RESOURCES_REPOSITORIES_PATH_ZIPPED_PATH, "git@github.com:AndreyLevchenko/jiragit-test-data.git");
        try {
            log.info(String.format("Unzipping [%s] repository to /%s", REPOSITORY_DISPLAY_NAME, PROXIED_REPO_LOCAL_FOLDER));
            repositoryImporter.unzipRepository(PROXIED_REPO_LOCAL_FOLDER);
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }
}
