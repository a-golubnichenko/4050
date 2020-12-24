package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.ao.model.UserPatEntry;
import com.bigbrassband.jira.git.services.async.ScanningProgressMonitor;
import com.bigbrassband.jira.git.services.globalsettings.MockGlobalSettingsManager;
import com.bigbrassband.jira.git.services.integration.ExternalApi;
import com.bigbrassband.jira.git.services.integration.ExternalApiService;
import com.bigbrassband.jira.git.services.integration.ExternalServiceFactory;
import com.bigbrassband.jira.git.services.integration.ExternalServiceFactoryImpl;
import com.bigbrassband.jira.git.services.integration.IntegrationPropertiesHolderImpl;
import com.bigbrassband.jira.git.services.integration.IntegrationType;
import com.bigbrassband.jira.git.services.integration.RepoApi;
import com.bigbrassband.jira.git.services.integration.model.ExternalRepository;
import com.bigbrassband.jira.git.services.props.GProperties;
import com.bigbrassband.jira.git.utils.MockCronExpressionValidator;
import com.bigbrassband.jira.git.utils.ValidationUtil;
import com.bigbrassband.jira.git.services.weblinks.WebLinkTemplateManagerImpl;
import com.bigbrassband.jira.git.utils.JiraUtils;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by isvirkina on 9/13/16.
 */
public class MockExternalServiceFactory {

    public static ExternalServiceFactory get(final ExternalApi externalServiceApi, final JiraUtils jiraUtils)
            throws IOException {
        return new ExternalServiceFactoryImpl(
                new WebLinkTemplateManagerImpl(),
                new IntegrationPropertiesHolderImpl(),
                MockGlobalSettingsManager.get(),
                new ValidationUtil(new MockI18nHelper(), null, jiraUtils, null, new MockCronExpressionValidator()),
                new MockI18nHelper()) {
            @Override
            public ExternalApiService buildApi(ExternalServiceProperties props) {

                return new ExternalApiService() {
                    @Override
                    public void validate() {
                        //do nothing
                    }

                    @Override
                    public Set<ExternalRepository> scan(ScanningProgressMonitor monitor) {
                        return new TreeSet<>(externalServiceApi.getRepositories(null, null, -1));
                    }

                    @Override
                    public Set<ExternalRepository> fastScan(ScanningProgressMonitor monitor) {
                        return new TreeSet<>(externalServiceApi.getRepositories(null, null, getMaxPingProjectCount()));
                    }

                    @Override
                    public void customizeParams(GProperties props) {
                    }

                    @Override
                    public RepoApi getRepoApi(String folderWithExternalRepositoryInfo) {
                        return null;
                    }

                    @Override
                    public IntegrationType isRepoApiSupported() {
                        return null;
                    }

                    @Override
                    public ExternalApiService clone(UserPatEntry userPat) {
                        return null;
                    }
                };
            }
        };
    }

}
