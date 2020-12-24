package com.bigbrassband.jira.git;

import com.atlassian.upm.api.license.entity.Contact;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.Organization;
import com.atlassian.upm.api.license.entity.Partner;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * Created by ababilo on 8/31/15.
 */
public class MockPluginLicense implements PluginLicense {

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Option<LicenseError> getError() {
        return Option.none();
    }

    @Override
    public String getRawLicense() {
        return null;
    }

    @Override
    public Option<Integer> getLicenseVersion() {
        return null;
    }

    @Override
    public String getPluginName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getServerId() {
        return null;
    }

    @Override
    public Organization getOrganization() {
        return null;
    }

    @Override
    public Option<Partner> getPartner() {
        return null;
    }

    @Override
    public Iterable<Contact> getContacts() {
        return null;
    }

    @Override
    public DateTime getCreationDate() {
        return null;
    }

    @Override
    public DateTime getPurchaseDate() {
        return null;
    }

    @Override
    public Option<DateTime> getExpiryDate() {
        return null;
    }

    @Override
    public Option<Period> getTimeBeforeExpiry() {
        return null;
    }

    @Override
    public Option<String> getSupportEntitlementNumber() {
        return null;
    }

    @Override
    public Option<DateTime> getMaintenanceExpiryDate() {
        return null;
    }

    @Override
    public Option<Period> getTimeBeforeMaintenanceExpiry() {
        return null;
    }

    @Override
    public Option<Integer> getMaximumNumberOfUsers() {
        return null;
    }

    @Override
    public Option<Integer> getEdition() {
        return null;
    }

    @Override
    public boolean isUnlimitedNumberOfUsers() {
        return false;
    }

    @Override
    public boolean isUnlimitedEdition() {
        return false;
    }

    @Override
    public boolean isEvaluation() {
        return false;
    }

    @Override
    public boolean isSubscription() {
        return false;
    }

    @Override
    public boolean isMaintenanceExpired() {
        return false;
    }

    @Override
    public LicenseType getLicenseType() {
        return null;
    }

    @Override
    public String getPluginKey() {
        return null;
    }

    @Override
    public boolean isEmbeddedWithinHostLicense() {
        return false;
    }
}
