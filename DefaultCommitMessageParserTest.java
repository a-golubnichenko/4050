package com.bigbrassband.jira.git;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.junit.rules.MockitoMocksInContainer;
import com.atlassian.jira.plugins.dvcs.smartcommits.DefaultCommitMessageParser;
import com.atlassian.jira.plugins.dvcs.smartcommits.DefaultSmartcommitsService;
import com.atlassian.jira.plugins.dvcs.smartcommits.GitPluginCompatibilityCrowdService;
import com.atlassian.jira.plugins.dvcs.smartcommits.handlers.AffectsVersionHandler;
import com.atlassian.jira.plugins.dvcs.smartcommits.handlers.AssignHandler;
import com.atlassian.jira.plugins.dvcs.smartcommits.handlers.CommentHandler;
import com.atlassian.jira.plugins.dvcs.smartcommits.handlers.FixVersionHandler;
import com.atlassian.jira.plugins.dvcs.smartcommits.handlers.LabelHandler;
import com.atlassian.jira.plugins.dvcs.smartcommits.handlers.TransitionHandler;
import com.atlassian.jira.plugins.dvcs.smartcommits.handlers.WorkLogHandler;
import com.atlassian.jira.plugins.dvcs.smartcommits.model.CommitCommands;
import com.atlassian.jira.plugins.dvcs.smartcommits.model.CommitHookHandlerError;
import com.atlassian.jira.plugins.dvcs.smartcommits.model.Either;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.bigbrassband.jira.git.services.scripting.ScriptTriggersHive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author isvirkina
 */
@RunWith(JUnit4.class)
public class DefaultCommitMessageParserTest {

    @Rule
    public RuleChain mocksInContainer = MockitoMocksInContainer.forTest(this);


    @Test
    /**
     * Models case: https://bigbrassband.freshdesk.com/helpdesk/tickets/3136
     */
    public void testSquashedCommitMessageInjection() throws IOException {
        GitPluginCompatibilityCrowdService crowdService = mock(GitPluginCompatibilityCrowdService.class);
        IssueManager issueManager = mock(IssueManager.class);
        TransitionHandler transitionHandler = mock(TransitionHandler.class);
        MutableIssue testIssue = mock(MutableIssue.class);
        when(testIssue.getProjectId()).thenReturn(10000L);

        MockWorklogService worklogService = MockWorklogService.create();

        WorkLogHandler workLogHandler = new WorkLogHandler(worklogService, new MockI18nHelper());
        when(issueManager.getIssueObject(eq("HUB-8"))).thenReturn(testIssue);
        ApplicationUser mockUser = mock(ApplicationUser.class);
        when(crowdService.getUserByEmailOrNull(eq("admin@example.com"), eq("Admin Adminoff")))
                .thenReturn(Collections.singletonList(mockUser));
        Either<CommitHookHandlerError, Issue> transitionResult = Either.value((Issue)testIssue);
        when(transitionHandler.handleCommand(any(ApplicationUser.class), any(MutableIssue.class), anyString(), any(List.class), any(Date.class)))
                .thenReturn(transitionResult);

        DefaultCommitMessageParser parser = new DefaultCommitMessageParser();

        String gitCommitMessage = "   1274b67 01: Toten Code entfernt HUB-8 #Done Toten Code entfernt #time 10m\n" +
                "  587d7fb Merge branch 'bugfix/pclint' of gitlab.workgroup.local:stoerk/D2_00_Huber_MPC";

        CommitCommands res = parser.parseCommitComment(gitCommitMessage);

        DefaultSmartcommitsService smartcommitsService = new DefaultSmartcommitsService(issueManager,
                transitionHandler,
                mock(CommentHandler.class),
                workLogHandler,
                mock(AssignHandler.class),
                mock(FixVersionHandler.class),
                mock(AffectsVersionHandler.class),
                mock(LabelHandler.class),
                mock(JiraAuthenticationContext.class),
                crowdService,
                mock(ScriptTriggersHive.class),
                new MockI18nHelper());
        res.setAuthorEmail("admin@example.com");
        res.setAuthorName("Admin Adminoff");

        smartcommitsService.doCommands(res, null);
        long actualTimeSpent = worklogService.getWorklog().getTimeSpent();
        assertEquals("Expected worklog time: 10 minutes", 600L, actualTimeSpent);
    }

}
