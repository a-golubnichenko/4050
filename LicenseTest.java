package com.bigbrassband.jira.git;

import com.atlassian.jira.util.I18nHelper;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.util.Option;
import com.bigbrassband.jira.git.jiraservices.licensing.LicensingUtils;
import com.bigbrassband.jira.git.jiraservices.licensing.MaintenanceBannerManager;
import com.bigbrassband.jira.git.jiraservices.licensing.MaintenanceBannerResource;
import com.bigbrassband.jira.git.services.permissions.GitPluginPermissionManager;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by ababilo on 9/17/15.
 */
@RunWith(Theories.class)
public class LicenseTest {

    private static I18nHelper i18nHelper;
    private GitPluginPermissionManager permissionManager;
    private MaintenanceBannerManager maintenanceBannerManager;

    @BeforeClass
    public static void loadTranslations() throws IOException {
        i18nHelper = new MockI18nHelper();
    }

    @Before
    public void setUp() {
        permissionManager = mock(GitPluginPermissionManager.class);
        doReturn(true).when(permissionManager).hasAdminAccess();

    }

    @Test
    public void valid() {
        PluginLicenseManager pluginLicenseManager = MockPluginLicenseManager.get();
        Assert.assertTrue(LicensingUtils.isLicensed(pluginLicenseManager));
    }

    @Test
    public void noLicense() {
        PluginLicenseManager pluginLicenseManager = MockPluginLicenseManager.get();
        doReturn(Option.none()).when(pluginLicenseManager).getLicense();
        Assert.assertFalse(LicensingUtils.isLicensed(pluginLicenseManager));
    }

    @DataPoints
    public static LicenseError[] errors = LicenseError.values();

    @Test
    @Theory
    public void error(final LicenseError error) {
        PluginLicenseManager pluginLicenseManager = MockPluginLicenseManager.get();
        doReturn(Option.some(new MockPluginLicense() {
            @Override
            public Option<LicenseError> getError() {
                return Option.some(error);
            }
        })).when(pluginLicenseManager).getLicense();
        Assert.assertFalse(LicensingUtils.isLicensed(pluginLicenseManager));

        // test existance of git.license.EXPIRED, git.license.TYPE_MISMATCH, git.license.USER_MISMATCH, git.license.EDITION_MISMATCH, git.license.VERSION_MISMATCH
        String licenseErrorLocation = LicensingUtils.getStatus(pluginLicenseManager);
        Assert.assertNotNull(licenseErrorLocation + " location doesn't exist", i18nHelper.getText(licenseErrorLocation));
    }

    private void checkInfo(MockPluginLicense license, Map<String, Object> expected, boolean dissmissed) throws Exception {
        PluginLicenseManager pluginLicenseManager = MockPluginLicenseManager.get(license);
        maintenanceBannerManager = spy(new MaintenanceBannerManager(pluginLicenseManager));
        doReturn(dissmissed).when(maintenanceBannerManager).isLicenseReminderDissmissed(any(HttpServletRequest.class));
        MaintenanceBannerResource api = new MaintenanceBannerResource(maintenanceBannerManager,
                permissionManager, i18nHelper);
        Response info = api.getMaintenanceInfo(mock(HttpServletRequest.class));
        Map<String, Object> entity = (HashMap) info.getEntity();
        System.out.println("Actual:\t\t" + entity.toString());
        System.out.println("Expected:\t" + expected.toString());

        Assert.assertTrue(expected.equals(entity));
    }

    @DataPoints
    public static boolean[] evaluation = new boolean[]{true, false};

    @Test
    @Theory
    public void normal(final boolean evaluation) throws Exception {
        MockPluginLicense license = new MockPluginLicense() {
            @Override
            public Option<DateTime> getMaintenanceExpiryDate() {
                return Option.some(new DateTime().plusDays(LicensingUtils.NOTIFICATION_PERIOD + 1).plusHours(1));
            }

            @Override
            public Option<DateTime> getExpiryDate() {
                return Option.some(new DateTime().plusDays(LicensingUtils.NOTIFICATION_PERIOD + 1).plusHours(1));
            }

            @Override
            public boolean isEvaluation() {
                return evaluation;
            }
        };

        Map<String, Object> expected = new HashMap<>();
        expected.put("success", "true");
        expected.put("show", false);
        expected.put("isMaintenanceExpired", false);
        expected.put("isLicenseExpired", false);
        expected.put("maintenanceDays", LicensingUtils.NOTIFICATION_PERIOD + 1);
        expected.put("licenseDays", LicensingUtils.NOTIFICATION_PERIOD + 1);
        expected.put("isEvaluation", evaluation);
        expected.put("licenseBannerMessage", i18nHelper.getText(evaluation
                ? "plugin.license.storage.admin.license.period.expires"
                : "plugin.license.storage.admin.maintenence.period.expires", LicensingUtils.NOTIFICATION_PERIOD + 1));
        checkInfo(license, expected, false);
    }

    @Test
    @Theory
    public void licenseExpired(final boolean evaluation) throws Exception {
        MockPluginLicense license = new MockPluginLicense() {
            @Override
            public Option<DateTime> getMaintenanceExpiryDate() {
                return Option.some(new DateTime().minusDays(1));
            }

            @Override
            public Option<DateTime> getExpiryDate() {
                return Option.some(new DateTime().minusDays(1));
            }

            @Override
            public boolean isMaintenanceExpired() {
                return !evaluation;
            }

            @Override
            public boolean isEvaluation() {
                return evaluation;
            }
        };

        Map<String, Object> expected = new HashMap<>();
        expected.put("success", "true");
        expected.put("show", true);
        expected.put("isMaintenanceExpired", !evaluation);
        expected.put("isLicenseExpired", true);
        expected.put("maintenanceDays", -1);
        expected.put("licenseDays", -1);
        expected.put("isEvaluation", evaluation);
        expected.put("licenseBannerMessage", i18nHelper.getText(evaluation
                ? "plugin.license.storage.admin.license.period.expired"
                : "plugin.license.storage.admin.maintenence.period.expired"));
        checkInfo(license, expected, false);
    }

    @Test
    @Theory
    public void licenseExpiresToday(final boolean evaluation) throws Exception {
        MockPluginLicense license = new MockPluginLicense() {
            @Override
            public Option<DateTime> getMaintenanceExpiryDate() {
                return Option.some(new DateTime().plusHours(1));
            }

            @Override
            public Option<DateTime> getExpiryDate() {
                return Option.some(new DateTime().plusHours(1)); // assumes that no daylight changes
            }

            @Override
            public boolean isEvaluation() {
                return evaluation;
            }
        };

        Map<String, Object> expected = new HashMap<>();
        expected.put("success", "true");
        expected.put("show", true);
        expected.put("isMaintenanceExpired", false);
        expected.put("isLicenseExpired", false);
        expected.put("maintenanceDays", 0);
        expected.put("licenseDays", 0);
        expected.put("isEvaluation", evaluation);
        expected.put("licenseBannerMessage", i18nHelper.getText(evaluation
                ? "plugin.license.storage.admin.license.period.expires.today"
                : "plugin.license.storage.admin.maintenence.period.expires.today"));
        checkInfo(license, expected, false);
    }

    @Test
    public void nonAdmin() throws Exception {
        doReturn(false).when(permissionManager).hasAdminAccess();
        MockPluginLicense license = new MockPluginLicense() {
            @Override
            public Option<DateTime> getMaintenanceExpiryDate() {
                return Option.some(new DateTime().plusDays(LicensingUtils.NOTIFICATION_PERIOD + 1).plusHours(1));
            }

            @Override
            public Option<DateTime> getExpiryDate() {
                return Option.some(new DateTime().plusDays(LicensingUtils.NOTIFICATION_PERIOD + 1).plusHours(1));
            }

            @Override
            public boolean isEvaluation() {
                return true;
            }
        };

        Map<String, Object> expected = new HashMap<>();
        expected.put("success", "true");
        expected.put("show", false);
        expected.put("isMaintenanceExpired", false);
        expected.put("isLicenseExpired", false);
        expected.put("maintenanceDays", LicensingUtils.NOTIFICATION_PERIOD + 1);
        expected.put("licenseDays", LicensingUtils.NOTIFICATION_PERIOD + 1);
        expected.put("isEvaluation", true);
        expected.put("licenseBannerMessage", i18nHelper.getText("plugin.license.storage.admin.license.period.expires",
                LicensingUtils.NOTIFICATION_PERIOD + 1));
        checkInfo(license, expected, false);
    }

    @Test
    public void dissmiss() throws Exception {
        MockPluginLicense license = new MockPluginLicense() {
            @Override
            public Option<DateTime> getMaintenanceExpiryDate() {
                return Option.some(new DateTime().plusHours(1));
            }

            @Override
            public Option<DateTime> getExpiryDate() {
                return Option.some(new DateTime().plusHours(1)); // assumes that no daylight changes
            }

            @Override
            public boolean isEvaluation() {
                return true;
            }
        };

        Map<String, Object> expected = new HashMap<>();
        expected.put("success", "true");
        expected.put("show", true);
        expected.put("isMaintenanceExpired", false);
        expected.put("isLicenseExpired", false);
        expected.put("maintenanceDays", 0);
        expected.put("licenseDays", 0);
        expected.put("isEvaluation", true);
        expected.put("licenseBannerMessage", i18nHelper.getText("plugin.license.storage.admin.license.period.expires.today"));
        checkInfo(license, expected, false);
        // dissmiss banner
        expected.put("show", false);
        checkInfo(license, expected, true);
    }
}
