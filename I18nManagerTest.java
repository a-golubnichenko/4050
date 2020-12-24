package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.utils.I18nManager;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

/**
 *
 * @author isvirkina
 */
public class I18nManagerTest {

    private I18nManager i18nManager;

    @Before
    public void setUp() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        i18nManager = new I18nManager(new MockI18nHelper(), null);
    }

    @Test
    public void testEnPluralForm() {
        String commi1 = i18nManager.getPluralText("git-plugin-webpanel.commits-count", 1);
        String commi2 = i18nManager.getPluralText("git-plugin-webpanel.commits-count", 2);
        String commi5 = i18nManager.getPluralText("git-plugin-webpanel.commits-count", 5);
        Assert.assertEquals("<b>1</b> commit", commi1);
        Assert.assertEquals("<b>2</b> commits", commi2);
        Assert.assertEquals("<b>5</b> commits", commi5);
    }

    @Test
    public void testRusPluralForm() throws IOException {
        i18nManager = new I18nManager(new MockI18nHelper(new Locale("ru", "ru")), null);

        String commi1 = i18nManager.getPluralText("git-plugin-webpanel.commits-count", 1);
        String commi2 = i18nManager.getPluralText("git-plugin-webpanel.commits-count", 2);
        String commi5 = i18nManager.getPluralText("git-plugin-webpanel.commits-count", 5);
        String commi125 = i18nManager.getPluralText("git-plugin-webpanel.commits-count", 125);
        Assert.assertEquals("<b>1</b> коммит", commi1);
        Assert.assertEquals("<b>2</b> коммита", commi2);
        Assert.assertEquals("<b>5</b> коммитов", commi5);
        Assert.assertEquals("<b>125</b> коммитов", commi125);
    }

    @Test
    public void testPluralFormWithoutPluralTranslation() throws IOException {
        i18nManager = new I18nManager(new MockI18nHelper(new Locale("pl", "pl")), null);

        String commi1 = i18nManager.getPluralText("git-plugin-webpanel.commits-count", 1);
        String commi2 = i18nManager.getPluralText("git-plugin-webpanel.commits-count", 2);
        Assert.assertEquals("<b>1</b> commit", commi1);
        Assert.assertEquals("<b>2</b> commity", commi2);
    }
}
