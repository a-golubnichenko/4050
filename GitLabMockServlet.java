package com.bigbrassband.jira.git;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nchernov on 31-May-17.
 */
public class GitLabMockServlet extends HttpServlet {
    final MutableInt projectsNotQueried = new MutableInt(1);
    final String projectsAnswer = "[\n" +
            "  {\n" +
            "    \"id\": 4,\n" +
            "    \"description\": null,\n" +
            "    \"default_branch\": \"master\",\n" +
            "    \"visibility\": \"private\",\n" +
            String.format("    \"ssh_url_to_repo\": \"git@localhost:8443/%s.git\",", MockGitServers.REPOSITORY_DISPLAY_NAME) +
            String.format("    \"http_url_to_repo\": \"https://localhost:8443/%s.git\",\n", MockGitServers.REPOSITORY_DISPLAY_NAME) +
            String.format("    \"web_url\": \"https://localhost:8443/%s.git\",\n", MockGitServers.REPOSITORY_DISPLAY_NAME) +
            "    \"namespace\": {\n" +
            "      \"id\": 3,\n" +
            "      \"name\": \"Test\",\n" +
            "      \"path\": \"test\",\n" +
            "      \"kind\": \"group\",\n" +
            "      \"full_path\": \"test\"\n" +
            "    }," +
            "    \"owner\": {\n" +
            "      \"id\": 3,\n" +
            "      \"name\": \"Test\",\n" +
            "      \"created_at\": \"2013-09-30T13:46:02Z\"\n" +
            "    },\n" +
            "    \"name\": \"Test test\",\n" +
            "    \"name_with_namespace\": \"Test / Test test\",\n" +
            "    \"merge_requests_enabled\": false\n" +
            "  }\n" +
            "]";
    final String sampleAnswer = "{\n" +
            "  \"name\": \"John Smith\",\n" +
            "  \"username\": \"john_smith\",\n" +
            "  \"id\": 32,\n" +
            "  \"state\": \"active\",\n" +
            "  \"identities\": [],\n" +
            "  \"can_create_group\": true,\n" +
            "  \"can_create_project\": true,\n" +
            "  \"two_factor_enabled\": false,\n" +
            "  \"private_token\": \"9koXpg98eAheJpvBs5tK\"\n" +
            "}";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IOUtils.write(sampleAnswer, resp.getOutputStream());
        resp.setContentType("application/json");
        resp.setStatus(200);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().contains("/api/v3/projects")) {
            if (projectsNotQueried.intValue() > 0) {
                IOUtils.write(projectsAnswer, resp.getOutputStream());
                projectsNotQueried.decrement();
            } else {
                IOUtils.write("[]", resp.getOutputStream());
                projectsNotQueried.increment();
            }
            resp.setContentType("application/json");
            resp.setStatus(200);
        } else {
            super.doGet(req, resp);
        }
    }
}
