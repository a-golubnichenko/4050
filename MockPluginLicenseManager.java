package com.bigbrassband.jira.git;

import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.util.Option;
import org.mockito.Mockito;

/**
 * @author isvirkina
 */
public class MockPluginLicenseManager {

    public static final PluginLicenseManager DEFAULT_PLUGIN_LICENSE_MANAGER = get();

    public static PluginLicenseManager get() {
        return get(new MockPluginLicense());
    }

    public static PluginLicenseManager get(MockPluginLicense license) {
        PluginLicenseManager licenseManager = Mockito.mock(PluginLicenseManager.class);
        Mockito.doReturn(Option.some(license)).when(licenseManager).getLicense();

        return licenseManager;
    }
}
