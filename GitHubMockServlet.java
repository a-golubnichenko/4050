package com.bigbrassband.jira.git;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nchernov on 21.07.17.
 */
public class GitHubMockServlet extends HttpServlet {
    final String reposAnswer = new String("[\n" +
            "  {\n" +
            "    \"id\": 93150194,\n" +
            "    \"name\": \"SingleRepo\",\n" +
            "    \"full_name\": \"nchernovtest/SingleRepo\",\n" +
            "    \"owner\": {\n" +
            "      \"login\": \"nchernovtest\",\n" +
            "      \"id\": 29142224,\n" +
            "      \"avatar_url\": \"https://avatars3.githubusercontent.com/u/29142224?v=4\",\n" +
            "      \"gravatar_id\": \"\",\n" +
            "      \"url\": \"https://api.github.com/users/nchernovtest\",\n" +
            "      \"html_url\": \"https://github.com/nchernovtest\",\n" +
            "      \"followers_url\": \"https://api.github.com/users/nchernovtest/followers\",\n" +
            "      \"following_url\": \"https://api.github.com/users/nchernovtest/following{/other_user}\",\n" +
            "      \"gists_url\": \"https://api.github.com/users/nchernovtest/gists{/gist_id}\",\n" +
            "      \"starred_url\": \"https://api.github.com/users/nchernovtest/starred{/owner}{/repo}\",\n" +
            "      \"subscriptions_url\": \"https://api.github.com/users/nchernovtest/subscriptions\",\n" +
            "      \"organizations_url\": \"https://api.github.com/users/nchernovtest/orgs\",\n" +
            "      \"repos_url\": \"https://api.github.com/users/nchernovtest/repos\",\n" +
            "      \"events_url\": \"https://api.github.com/users/nchernovtest/events{/privacy}\",\n" +
            "      \"received_events_url\": \"https://api.github.com/users/nchernovtest/received_events\",\n" +
            "      \"type\": \"User\",\n" +
            "      \"site_admin\": false\n" +
            "    },\n" +
            "    \"private\": false,\n" +
            "    \"html_url\": \"%s\",\n" +
            "    \"description\": \"Single Project for test purposes\",\n" +
            "    \"fork\": false,\n" +
            "    \"url\": \"https://localhost:8443/repo/api/v3/repos/nchernovtest/SingleRepo\",\n" +
            "    \"forks_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/forks\",\n" +
            "    \"keys_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/keys{/key_id}\",\n" +
            "    \"collaborators_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/collaborators{/collaborator}\",\n" +
            "    \"teams_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/teams\",\n" +
            "    \"hooks_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/hooks\",\n" +
            "    \"issue_events_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/events{/number}\",\n" +
            "    \"events_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/events\",\n" +
            "    \"assignees_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/assignees{/user}\",\n" +
            "    \"branches_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/branches{/branch}\",\n" +
            "    \"tags_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/tags\",\n" +
            "    \"blobs_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/blobs{/sha}\",\n" +
            "    \"git_tags_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/tags{/sha}\",\n" +
            "    \"git_refs_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/refs{/sha}\",\n" +
            "    \"trees_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/trees{/sha}\",\n" +
            "    \"statuses_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/statuses/{sha}\",\n" +
            "    \"languages_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/languages\",\n" +
            "    \"stargazers_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/stargazers\",\n" +
            "    \"contributors_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/contributors\",\n" +
            "    \"subscribers_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/subscribers\",\n" +
            "    \"subscription_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/subscription\",\n" +
            "    \"commits_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/commits{/sha}\",\n" +
            "    \"git_commits_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/commits{/sha}\",\n" +
            "    \"comments_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/comments{/number}\",\n" +
            "    \"issue_comment_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/comments{/number}\",\n" +
            "    \"contents_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/contents/{+path}\",\n" +
            "    \"compare_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/compare/{base}...{head}\",\n" +
            "    \"merges_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/merges\",\n" +
            "    \"archive_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/{archive_format}{/ref}\",\n" +
            "    \"downloads_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/downloads\",\n" +
            "    \"issues_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues{/number}\",\n" +
            "    \"pulls_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls{/number}\",\n" +
            "    \"milestones_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/milestones{/number}\",\n" +
            "    \"notifications_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/notifications{?since,all,participating}\",\n" +
            "    \"labels_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/labels{/name}\",\n" +
            "    \"releases_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/releases{/id}\",\n" +
            "    \"deployments_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/deployments\",\n" +
            "    \"created_at\": \"2017-06-02T09:30:10Z\",\n" +
            "    \"updated_at\": \"2017-06-02T09:30:10Z\",\n" +
            "    \"pushed_at\": \"2017-06-20T07:41:21Z\",\n" +
            "    \"git_url\": \"%s\",\n" +
            "    \"ssh_url\": \"%s\",\n" +
            "    \"clone_url\": \"%s\",\n" +
            "    \"svn_url\": \"%s\",\n" +
            "    \"homepage\": null,\n" +
            "    \"size\": 0,\n" +
            "    \"stargazers_count\": 0,\n" +
            "    \"watchers_count\": 0,\n" +
            "    \"language\": null,\n" +
            "    \"has_issues\": true,\n" +
            "    \"has_projects\": true,\n" +
            "    \"has_downloads\": true,\n" +
            "    \"has_wiki\": true,\n" +
            "    \"has_pages\": false,\n" +
            "    \"forks_count\": 0,\n" +
            "    \"mirror_url\": null,\n" +
            "    \"open_issues_count\": 0,\n" +
            "    \"forks\": 0,\n" +
            "    \"open_issues\": 0,\n" +
            "    \"watchers\": 0,\n" +
            "    \"default_branch\": \"master\",\n" +
            "    \"permissions\": {\n" +
            "      \"admin\": true,\n" +
            "      \"push\": true,\n" +
            "      \"pull\": true\n" +
            "    }\n" +
            "  }\n" +
            "]\n").replaceAll("[%][s]", String.format("https://localhost:8443/%s.git", MockGitServers.REPOSITORY_DISPLAY_NAME));

    final String pullReqsAnswer = "[{\n" +
            "    \"patch_url\": \"https://github.com/nchernovtest/SingleRepo/pull/1.patch\",\n" +
            "    \"diff_url\": \"https://github.com/nchernovtest/SingleRepo/pull/1.diff\",\n" +
            "    \"body\": \"\",\n" +
            "    \"assignees\": [],\n" +
            "    \"state\": \"open\",\n" +
            "    \"commits_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls/1/commits\",\n" +
            "    \"_links\": {\n" +
            "        \"issue\": {\"href\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/1\"},\n" +
            "        \"commits\": {\"href\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls/1/commits\"},\n" +
            "        \"html\": {\"href\": \"https://github.com/nchernovtest/SingleRepo/pull/1\"},\n" +
            "        \"self\": {\"href\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls/1\"},\n" +
            "        \"review_comments\": {\"href\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls/1/comments\"},\n" +
            "        \"statuses\": {\"href\": \"https://api.github.com/repos/nchernovtest/SingleRepo/statuses/2890c2cecf7fc03c61a680c1bc9bf8e23a26fc47\"},\n" +
            "        \"review_comment\": {\"href\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls/comments{/number}\"},\n" +
            "        \"comments\": {\"href\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/1/comments\"}\n" +
            "    },\n" +
            "    \"issue_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/1\",\n" +
            "    \"id\": 153503547,\n" +
            "    \"milestone\": null,\n" +
            "    \"title\": \"TST-2 new changes\",\n" +
            "    \"comments_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/1/comments\",\n" +
            "    \"created_at\": \"2017-11-19T16:23:28Z\",\n" +
            "    \"review_comment_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls/comments{/number}\",\n" +
            "    \"head\": {\n" +
            "        \"ref\": \"TST-2\",\n" +
            "        \"sha\": \"2890c2cecf7fc03c61a680c1bc9bf8e23a26fc47\",\n" +
            "        \"repo\": {\n" +
            "            \"has_issues\": true,\n" +
            "            \"teams_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/teams\",\n" +
            "            \"compare_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/compare/{base}...{head}\",\n" +
            "            \"releases_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/releases{/id}\",\n" +
            "            \"keys_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/keys{/key_id}\",\n" +
            "            \"has_pages\": false,\n" +
            "            \"description\": \"Single Project for test purposes\",\n" +
            "            \"milestones_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/milestones{/number}\",\n" +
            "            \"has_wiki\": true,\n" +
            "            \"events_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/events\",\n" +
            "            \"archive_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/{archive_format}{/ref}\",\n" +
            "            \"subscribers_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/subscribers\",\n" +
            "            \"contributors_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/contributors\",\n" +
            "            \"pushed_at\": \"2017-11-20T07:28:13Z\",\n" +
            "            \"fork\": false,\n" +
            "            \"svn_url\": \"https://github.com/nchernovtest/SingleRepo\",\n" +
            "            \"collaborators_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/collaborators{/collaborator}\",\n" +
            "            \"subscription_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/subscription\",\n" +
            "            \"clone_url\": \"https://github.com/nchernovtest/SingleRepo.git\",\n" +
            "            \"trees_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/trees{/sha}\",\n" +
            "            \"homepage\": null,\n" +
            "            \"url\": \"https://api.github.com/repos/nchernovtest/SingleRepo\",\n" +
            "            \"size\": 1,\n" +
            "            \"notifications_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/notifications{?since,all,participating}\",\n" +
            "            \"deployments_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/deployments\",\n" +
            "            \"updated_at\": \"2017-08-07T05:07:55Z\",\n" +
            "            \"branches_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/branches{/branch}\",\n" +
            "            \"owner\": {\n" +
            "                \"received_events_url\": \"https://api.github.com/users/nchernovtest/received_events\",\n" +
            "                \"organizations_url\": \"https://api.github.com/users/nchernovtest/orgs\",\n" +
            "                \"avatar_url\": \"https://avatars3.githubusercontent.com/u/29142224?v=4\",\n" +
            "                \"gravatar_id\": \"\",\n" +
            "                \"gists_url\": \"https://api.github.com/users/nchernovtest/gists{/gist_id}\",\n" +
            "                \"starred_url\": \"https://api.github.com/users/nchernovtest/starred{/owner}{/repo}\",\n" +
            "                \"site_admin\": false,\n" +
            "                \"type\": \"User\",\n" +
            "                \"url\": \"https://api.github.com/users/nchernovtest\",\n" +
            "                \"id\": 29142224,\n" +
            "                \"html_url\": \"https://github.com/nchernovtest\",\n" +
            "                \"following_url\": \"https://api.github.com/users/nchernovtest/following{/other_user}\",\n" +
            "                \"events_url\": \"https://api.github.com/users/nchernovtest/events{/privacy}\",\n" +
            "                \"login\": \"nchernovtest\",\n" +
            "                \"subscriptions_url\": \"https://api.github.com/users/nchernovtest/subscriptions\",\n" +
            "                \"repos_url\": \"https://api.github.com/users/nchernovtest/repos\",\n" +
            "                \"followers_url\": \"https://api.github.com/users/nchernovtest/followers\"\n" +
            "            },\n" +
            "            \"issue_events_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/events{/number}\",\n" +
            "            \"language\": \"JavaScript\",\n" +
            "            \"forks_count\": 0,\n" +
            "            \"contents_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/contents/{+path}\",\n" +
            "            \"watchers_count\": 0,\n" +
            "            \"blobs_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/blobs{/sha}\",\n" +
            "            \"commits_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/commits{/sha}\",\n" +
            "            \"has_downloads\": true,\n" +
            "            \"git_commits_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/commits{/sha}\",\n" +
            "            \"private\": false,\n" +
            "            \"default_branch\": \"master\",\n" +
            "            \"open_issues\": 1,\n" +
            "            \"id\": 93150194,\n" +
            "            \"downloads_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/downloads\",\n" +
            "            \"mirror_url\": null,\n" +
            "            \"has_projects\": true,\n" +
            "            \"archived\": false,\n" +
            "            \"comments_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/comments{/number}\",\n" +
            "            \"name\": \"SingleRepo\",\n" +
            "            \"created_at\": \"2017-06-02T09:30:10Z\",\n" +
            "            \"stargazers_count\": 0,\n" +
            "            \"assignees_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/assignees{/user}\",\n" +
            "            \"pulls_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls{/number}\",\n" +
            "            \"watchers\": 0,\n" +
            "            \"stargazers_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/stargazers\",\n" +
            "            \"hooks_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/hooks\",\n" +
            "            \"languages_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/languages\",\n" +
            "            \"issues_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues{/number}\",\n" +
            "            \"git_tags_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/tags{/sha}\",\n" +
            "            \"merges_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/merges\",\n" +
            "            \"git_refs_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/refs{/sha}\",\n" +
            "            \"open_issues_count\": 1,\n" +
            "            \"ssh_url\": \"git@github.com:nchernovtest/SingleRepo.git\",\n" +
            "            \"html_url\": \"https://github.com/nchernovtest/SingleRepo\",\n" +
            "            \"forks\": 0,\n" +
            "            \"statuses_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/statuses/{sha}\",\n" +
            "            \"forks_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/forks\",\n" +
            "            \"issue_comment_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/comments{/number}\",\n" +
            "            \"labels_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/labels{/name}\",\n" +
            "            \"git_url\": \"git://github.com/nchernovtest/SingleRepo.git\",\n" +
            "            \"tags_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/tags\",\n" +
            "            \"full_name\": \"nchernovtest/SingleRepo\"\n" +
            "        },\n" +
            "        \"label\": \"nchernovtest:TST-2\",\n" +
            "        \"user\": {\n" +
            "            \"received_events_url\": \"https://api.github.com/users/nchernovtest/received_events\",\n" +
            "            \"organizations_url\": \"https://api.github.com/users/nchernovtest/orgs\",\n" +
            "            \"avatar_url\": \"https://avatars3.githubusercontent.com/u/29142224?v=4\",\n" +
            "            \"gravatar_id\": \"\",\n" +
            "            \"gists_url\": \"https://api.github.com/users/nchernovtest/gists{/gist_id}\",\n" +
            "            \"starred_url\": \"https://api.github.com/users/nchernovtest/starred{/owner}{/repo}\",\n" +
            "            \"site_admin\": false,\n" +
            "            \"type\": \"User\",\n" +
            "            \"url\": \"https://api.github.com/users/nchernovtest\",\n" +
            "            \"id\": 29142224,\n" +
            "            \"html_url\": \"https://github.com/nchernovtest\",\n" +
            "            \"following_url\": \"https://api.github.com/users/nchernovtest/following{/other_user}\",\n" +
            "            \"events_url\": \"https://api.github.com/users/nchernovtest/events{/privacy}\",\n" +
            "            \"login\": \"nchernovtest\",\n" +
            "            \"subscriptions_url\": \"https://api.github.com/users/nchernovtest/subscriptions\",\n" +
            "            \"repos_url\": \"https://api.github.com/users/nchernovtest/repos\",\n" +
            "            \"followers_url\": \"https://api.github.com/users/nchernovtest/followers\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"merged_at\": null,\n" +
            "    \"closed_at\": null,\n" +
            "    \"review_comments_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls/1/comments\",\n" +
            "    \"author_association\": \"OWNER\",\n" +
            "    \"assignee\": null,\n" +
            "    \"number\": 1,\n" +
            "    \"url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls/1\",\n" +
            "    \"html_url\": \"https://github.com/nchernovtest/SingleRepo/pull/1\",\n" +
            "    \"updated_at\": \"2017-11-19T16:23:28Z\",\n" +
            "    \"base\": {\n" +
            "        \"ref\": \"master\",\n" +
            "        \"sha\": \"f105edfafd7a232ee19f87e97399b2db3030ae3d\",\n" +
            "        \"repo\": {\n" +
            "            \"has_issues\": true,\n" +
            "            \"teams_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/teams\",\n" +
            "            \"compare_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/compare/{base}...{head}\",\n" +
            "            \"releases_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/releases{/id}\",\n" +
            "            \"keys_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/keys{/key_id}\",\n" +
            "            \"has_pages\": false,\n" +
            "            \"description\": \"Single Project for test purposes\",\n" +
            "            \"milestones_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/milestones{/number}\",\n" +
            "            \"has_wiki\": true,\n" +
            "            \"events_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/events\",\n" +
            "            \"archive_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/{archive_format}{/ref}\",\n" +
            "            \"subscribers_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/subscribers\",\n" +
            "            \"contributors_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/contributors\",\n" +
            "            \"pushed_at\": \"2017-11-20T07:28:13Z\",\n" +
            "            \"fork\": false,\n" +
            "            \"svn_url\": \"https://github.com/nchernovtest/SingleRepo\",\n" +
            "            \"collaborators_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/collaborators{/collaborator}\",\n" +
            "            \"subscription_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/subscription\",\n" +
            "            \"clone_url\": \"https://github.com/nchernovtest/SingleRepo.git\",\n" +
            "            \"trees_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/trees{/sha}\",\n" +
            "            \"homepage\": null,\n" +
            "            \"url\": \"https://api.github.com/repos/nchernovtest/SingleRepo\",\n" +
            "            \"size\": 1,\n" +
            "            \"notifications_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/notifications{?since,all,participating}\",\n" +
            "            \"deployments_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/deployments\",\n" +
            "            \"updated_at\": \"2017-08-07T05:07:55Z\",\n" +
            "            \"branches_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/branches{/branch}\",\n" +
            "            \"owner\": {\n" +
            "                \"received_events_url\": \"https://api.github.com/users/nchernovtest/received_events\",\n" +
            "                \"organizations_url\": \"https://api.github.com/users/nchernovtest/orgs\",\n" +
            "                \"avatar_url\": \"https://avatars3.githubusercontent.com/u/29142224?v=4\",\n" +
            "                \"gravatar_id\": \"\",\n" +
            "                \"gists_url\": \"https://api.github.com/users/nchernovtest/gists{/gist_id}\",\n" +
            "                \"starred_url\": \"https://api.github.com/users/nchernovtest/starred{/owner}{/repo}\",\n" +
            "                \"site_admin\": false,\n" +
            "                \"type\": \"User\",\n" +
            "                \"url\": \"https://api.github.com/users/nchernovtest\",\n" +
            "                \"id\": 29142224,\n" +
            "                \"html_url\": \"https://github.com/nchernovtest\",\n" +
            "                \"following_url\": \"https://api.github.com/users/nchernovtest/following{/other_user}\",\n" +
            "                \"events_url\": \"https://api.github.com/users/nchernovtest/events{/privacy}\",\n" +
            "                \"login\": \"nchernovtest\",\n" +
            "                \"subscriptions_url\": \"https://api.github.com/users/nchernovtest/subscriptions\",\n" +
            "                \"repos_url\": \"https://api.github.com/users/nchernovtest/repos\",\n" +
            "                \"followers_url\": \"https://api.github.com/users/nchernovtest/followers\"\n" +
            "            },\n" +
            "            \"issue_events_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/events{/number}\",\n" +
            "            \"language\": \"JavaScript\",\n" +
            "            \"forks_count\": 0,\n" +
            "            \"contents_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/contents/{+path}\",\n" +
            "            \"watchers_count\": 0,\n" +
            "            \"blobs_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/blobs{/sha}\",\n" +
            "            \"commits_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/commits{/sha}\",\n" +
            "            \"has_downloads\": true,\n" +
            "            \"git_commits_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/commits{/sha}\",\n" +
            "            \"private\": false,\n" +
            "            \"default_branch\": \"master\",\n" +
            "            \"open_issues\": 1,\n" +
            "            \"id\": 93150194,\n" +
            "            \"downloads_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/downloads\",\n" +
            "            \"mirror_url\": null,\n" +
            "            \"has_projects\": true,\n" +
            "            \"archived\": false,\n" +
            "            \"comments_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/comments{/number}\",\n" +
            "            \"name\": \"SingleRepo\",\n" +
            "            \"created_at\": \"2017-06-02T09:30:10Z\",\n" +
            "            \"stargazers_count\": 0,\n" +
            "            \"assignees_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/assignees{/user}\",\n" +
            "            \"pulls_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/pulls{/number}\",\n" +
            "            \"watchers\": 0,\n" +
            "            \"stargazers_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/stargazers\",\n" +
            "            \"hooks_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/hooks\",\n" +
            "            \"languages_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/languages\",\n" +
            "            \"issues_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues{/number}\",\n" +
            "            \"git_tags_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/tags{/sha}\",\n" +
            "            \"merges_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/merges\",\n" +
            "            \"git_refs_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/git/refs{/sha}\",\n" +
            "            \"open_issues_count\": 1,\n" +
            "            \"ssh_url\": \"git@github.com:nchernovtest/SingleRepo.git\",\n" +
            "            \"html_url\": \"https://github.com/nchernovtest/SingleRepo\",\n" +
            "            \"forks\": 0,\n" +
            "            \"statuses_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/statuses/{sha}\",\n" +
            "            \"forks_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/forks\",\n" +
            "            \"issue_comment_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/issues/comments{/number}\",\n" +
            "            \"labels_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/labels{/name}\",\n" +
            "            \"git_url\": \"git://github.com/nchernovtest/SingleRepo.git\",\n" +
            "            \"tags_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/tags\",\n" +
            "            \"full_name\": \"nchernovtest/SingleRepo\"\n" +
            "        },\n" +
            "        \"label\": \"nchernovtest:master\",\n" +
            "        \"user\": {\n" +
            "            \"received_events_url\": \"https://api.github.com/users/nchernovtest/received_events\",\n" +
            "            \"organizations_url\": \"https://api.github.com/users/nchernovtest/orgs\",\n" +
            "            \"avatar_url\": \"https://avatars3.githubusercontent.com/u/29142224?v=4\",\n" +
            "            \"gravatar_id\": \"\",\n" +
            "            \"gists_url\": \"https://api.github.com/users/nchernovtest/gists{/gist_id}\",\n" +
            "            \"starred_url\": \"https://api.github.com/users/nchernovtest/starred{/owner}{/repo}\",\n" +
            "            \"site_admin\": false,\n" +
            "            \"type\": \"User\",\n" +
            "            \"url\": \"https://api.github.com/users/nchernovtest\",\n" +
            "            \"id\": 29142224,\n" +
            "            \"html_url\": \"https://github.com/nchernovtest\",\n" +
            "            \"following_url\": \"https://api.github.com/users/nchernovtest/following{/other_user}\",\n" +
            "            \"events_url\": \"https://api.github.com/users/nchernovtest/events{/privacy}\",\n" +
            "            \"login\": \"nchernovtest\",\n" +
            "            \"subscriptions_url\": \"https://api.github.com/users/nchernovtest/subscriptions\",\n" +
            "            \"repos_url\": \"https://api.github.com/users/nchernovtest/repos\",\n" +
            "            \"followers_url\": \"https://api.github.com/users/nchernovtest/followers\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"statuses_url\": \"https://api.github.com/repos/nchernovtest/SingleRepo/statuses/2890c2cecf7fc03c61a680c1bc9bf8e23a26fc47\",\n" +
            "    \"locked\": false,\n" +
            "    \"requested_reviewers\": [],\n" +
            "    \"user\": {\n" +
            "        \"received_events_url\": \"https://api.github.com/users/nchernovtest/received_events\",\n" +
            "        \"organizations_url\": \"https://api.github.com/users/nchernovtest/orgs\",\n" +
            "        \"avatar_url\": \"https://avatars3.githubusercontent.com/u/29142224?v=4\",\n" +
            "        \"gravatar_id\": \"\",\n" +
            "        \"gists_url\": \"https://api.github.com/users/nchernovtest/gists{/gist_id}\",\n" +
            "        \"starred_url\": \"https://api.github.com/users/nchernovtest/starred{/owner}{/repo}\",\n" +
            "        \"site_admin\": false,\n" +
            "        \"type\": \"User\",\n" +
            "        \"url\": \"https://api.github.com/users/nchernovtest\",\n" +
            "        \"id\": 29142224,\n" +
            "        \"html_url\": \"https://github.com/nchernovtest\",\n" +
            "        \"following_url\": \"https://api.github.com/users/nchernovtest/following{/other_user}\",\n" +
            "        \"events_url\": \"https://api.github.com/users/nchernovtest/events{/privacy}\",\n" +
            "        \"login\": \"nchernovtest\",\n" +
            "        \"subscriptions_url\": \"https://api.github.com/users/nchernovtest/subscriptions\",\n" +
            "        \"repos_url\": \"https://api.github.com/users/nchernovtest/repos\",\n" +
            "        \"followers_url\": \"https://api.github.com/users/nchernovtest/followers\"\n" +
            "    },\n" +
            "    \"merge_commit_sha\": \"b298caef39a27f3873a54e9dcb94f87907ba5f00\"\n" +
            "}]";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().contains("/user/repos")) {
            IOUtils.write(reposAnswer, resp.getOutputStream());
            resp.setContentType("application/json");
            resp.setStatus(200);
        } else if (req.getRequestURI().contains("/nchernovtest/SingleRepo/pulls")) {
            IOUtils.write(pullReqsAnswer, resp.getOutputStream());
            resp.setContentType("application/json");
            resp.setStatus(200);
        } else {
            super.doGet(req, resp);
        }
    }
}
