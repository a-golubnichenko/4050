package com.bigbrassband.jira.git;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;


public class MockGitServlet extends GitServlet {
    private static int delay = 0;

    public static void setDelay(int delay) {
        MockGitServlet.delay = delay;
    }

    public MockGitServlet() {
        super();

        setRepositoryResolver(new RepositoryResolver<HttpServletRequest>() {
            public Repository open(HttpServletRequest req, String name)
                    throws RepositoryNotFoundException,
                    ServiceNotEnabledException {

                try {
                    if (delay > 0) {
                        try {
                            Thread.sleep(delay * 1000L);
                        } catch (InterruptedException e) {
                            //swallow silently
                        }
                    }
                    return FileRepositoryBuilder.create(new File(name + "/.git"));
                } catch (IOException e) {
                    throw new RepositoryNotFoundException(name);
                }
            }
        });


    }


}
