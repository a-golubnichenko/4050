package com.bigbrassband.jira.git;

import com.atlassian.jira.util.I18nHelper;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ababilo on 9/18/15.
 */
public class MockI18nHelper implements I18nHelper {

    private Locale locale;
    private Map<String, String> translations;

    private MockI18nHelper(String localeAbbr) throws IOException {
        Properties props = new Properties();
        props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("messages/localization" + localeAbbr + ".properties"));

        this.translations = Maps.fromProperties(props);
    }

    public MockI18nHelper(Locale locale) throws IOException {
        this("_" + locale.getCountry().toLowerCase());
        this.locale = locale;
    }

    public MockI18nHelper() throws IOException {
        this("");
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public ResourceBundle getDefaultResourceBundle() {
        return null;
    }

    @Override
    public String getUnescapedText(String s) {
        return translations.get(s);
    }

    @Override
    public String getUntransformedRawText(String s) {
        return translations.get(s);
    }

    @Override
    public boolean isKeyDefined(String s) {
        return translations.containsKey(s);
    }

    @Override
    public String getText(String s) {
        return translations.get(s);
    }

    @Override
    public String getText(String s, String s1) {
        return MessageFormat.format(translations.get(s), s1);
    }

    @Override
    public String getText(String s, String s1, String s2) {
        return MessageFormat.format(translations.get(s), s1, s2);
    }

    @Override
    public String getText(String s, String s1, String s2, String s3) {
        return MessageFormat.format(translations.get(s), s1, s2, s3);
    }

    @Override
    public String getText(String s, String s1, String s2, String s3, String s4) {
        return MessageFormat.format(translations.get(s), s1, s2, s3, s4);
    }

    @Override
    public String getText(String s, Object o, Object o1, Object o2) {
        return MessageFormat.format(translations.get(s), o, o1, o2);
    }

    @Override
    public String getText(String s, Object o, Object o1, Object o2, Object o3) {
        return MessageFormat.format(translations.get(s), o, o1, o2, o3);
    }

    @Override
    public String getText(String s, Object o, Object o1, Object o2, Object o3, Object o4) {
        return MessageFormat.format(translations.get(s), o, o1, o2, o3, o4);
    }

    @Override
    public String getText(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
        return MessageFormat.format(translations.get(s), o, o1, o2, o3, o4, o5);
    }

    @Override
    public String getText(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        return MessageFormat.format(translations.get(s), o, o1, o2, o3, o4, o5, o6);
    }

    @Override
    public String getText(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
        return MessageFormat.format(translations.get(s), s1, s2, s3, s4, s5, s6, s7);
    }

    @Override
    public String getText(String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        return MessageFormat.format(translations.get(s), o, o1, o2, o3, o4, o5, o6, o7);
    }

    @Override
    public String getText(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9) {
        return MessageFormat.format(translations.get(s), s1, s2, s3, s4, s5, s6, s7, s8, s9);
    }

    @Override
    public String getText(String s, Object o) {
        return MessageFormat.format(translations.get(s), o);
    }

    @Override
    public Set<String> getKeysForPrefix(final String s) {
        return translations.keySet().stream()
                                .filter(key -> key.startsWith(s))
                                .collect(Collectors.toSet());
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return null;
    }
}
