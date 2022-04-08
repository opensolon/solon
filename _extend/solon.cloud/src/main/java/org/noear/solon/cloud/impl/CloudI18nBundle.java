package org.noear.solon.cloud.impl;

import org.noear.solon.i18n.I18nBundle;

import java.util.Locale;
import java.util.Map;

/**
 * @author noear
 * @since 1.6
 */
public class CloudI18nBundle implements I18nBundle {
    Map<String, String> data;
    Locale locale;

    public CloudI18nBundle(Map<String, String> data, Locale locale) {
        this.data = data;
        this.locale = locale;
    }

    @Override
    public Map<String, String> toMap() {
        return data;
    }

    @Override
    public Locale locale() {
        return locale;
    }

    @Override
    public String get(String key) {
        return data.get(key);
    }
}
