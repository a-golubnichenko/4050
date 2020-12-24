package com.bigbrassband.jira.git;

import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.issue.util.VisibilityValidator;
import com.atlassian.jira.bc.issue.worklog.DefaultWorklogService;
import com.atlassian.jira.bc.issue.worklog.TimeTrackingConfiguration;
import com.atlassian.jira.bc.issue.worklog.WorklogInputParameters;
import com.atlassian.jira.bc.issue.worklog.WorklogResult;
import com.atlassian.jira.bc.issue.worklog.WorklogResultFactory;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogImpl;
import com.atlassian.jira.issue.worklog.WorklogManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.JiraDurationUtils;

import java.math.BigDecimal;
import java.util.Locale;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author dmalyshkin
 */
public class MockWorklogService extends DefaultWorklogService {

    JiraServiceContext mockJiraServiceContext = mock(JiraServiceContext.class);
    I18nHelper i18nHelper = mock(I18nHelper.class);
    ApplicationUser applicationUser = mock(ApplicationUser.class);
    private WorklogImpl worklog;

    public static MockWorklogService create() {
        ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
        TimeTrackingConfiguration timeTrackingConfiguration = mock(TimeTrackingConfiguration.class);
        when(applicationProperties.getDefaultBackedString(eq("jira.timetracking.format"))).thenReturn("hours");
        when(timeTrackingConfiguration.getDaysPerWeek()).thenReturn(new BigDecimal(5));
        when(timeTrackingConfiguration.getHoursPerDay()).thenReturn(new BigDecimal(8));

        JiraDurationUtils jiraDurationUtils = new JiraDurationUtils(applicationProperties,
                mock(JiraAuthenticationContext.class),
                timeTrackingConfiguration,
                mock(EventPublisher.class),
                mock(I18nHelper.BeanFactory.class),
                mock(CacheManager.class));

        return new MockWorklogService(null, null, null, null, null, null, jiraDurationUtils, null);
    }

    private MockWorklogService(WorklogManager worklogManager, PermissionManager permissionManager,
                              VisibilityValidator visibilityValidator, ProjectRoleManager projectRoleManager,
                              IssueManager issueManager, TimeTrackingConfiguration timeTrackingConfiguration,
                              JiraDurationUtils jiraDurationUtils, GroupManager groupManager) {
        super(worklogManager, permissionManager, visibilityValidator, projectRoleManager, issueManager,
                timeTrackingConfiguration, jiraDurationUtils, groupManager);

        when(mockJiraServiceContext.getI18nBean()).thenReturn(i18nHelper);
        when(mockJiraServiceContext.getLoggedInApplicationUser()).thenReturn(applicationUser);
        when(i18nHelper.getLocale()).thenReturn(Locale.US);
    }

    @Override
    protected Worklog create(JiraServiceContext jiraServiceContext, WorklogResult worklogResult, Long newEstimate, boolean dispatchEvent) {
        return worklog;
    }

    @Override
    public WorklogResult validateCreate(JiraServiceContext jiraServiceContext, WorklogInputParameters params) {
        worklog = new WorklogImpl(mock(WorklogManager.class), params.getIssue(), 100500L, "", params.getComment(), params.getStartDate(), null, null, this.getDurationForFormattedString(params.getTimeSpent(), mockJiraServiceContext), null, null, null);
        return WorklogResultFactory.create(worklog, params.isEditableCheckRequired());
    }

    public WorklogImpl getWorklog() {
        return worklog;
    }

}
