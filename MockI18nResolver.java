package com.bigbrassband.jira.git;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.message.MessageCollection;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class MockI18nResolver implements I18nResolver {

    private Map<String, String> translations;

    public MockI18nResolver() throws IOException {
        this(null);
    }

    public MockI18nResolver(Locale locale) throws IOException {
        Properties props = new Properties();
        String localeSuffix = locale == null ? "" : "_" + locale.getCountry().toLowerCase();
        props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "messages/localization" + localeSuffix + ".properties"));
        this.translations = Maps.fromProperties(props);
    }

    @Override
    public String getRawText(String s) {
        return null;
    }

    @Override
    public String getText(String s, Serializable... serializables) {
        return null;
    }

    @Override
    public String getText(String s) {
        return null;
    }

    @Override
    public String getText(Message message) {
        return null;
    }

    @Override
    public Message createMessage(String s, Serializable... serializables) {
        return null;
    }

    @Override
    public MessageCollection createMessageCollection() {
        return null;
    }

    @Override
    public Map<String, String> getAllTranslationsForPrefix(String s) {
        return translations;
    }

    @Override
    public Map<String, String> getAllTranslationsForPrefix(String s, Locale locale) {
        return translations;
    }
}
