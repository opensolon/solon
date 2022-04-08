package org.noear.solon.cloud.model;

import java.util.Locale;

/**
 * @author noear
 * @since 1.6
 */
public class PackHolder {
    private final String group;
    private final String bundleName;
    private final Locale locale;
    private final String lang;
    private final Pack pack;

    public PackHolder(String group, String bundleName, Locale locale) {
        this.group = group;
        this.bundleName = bundleName;
        this.locale = locale;
        this.lang = locale.toString();
        this.pack = new Pack();
    }

    public String getGroup() {
        return group;
    }

    public String getBundleName() {
        return bundleName;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLang() {
        return lang;
    }

    public Pack getPack() {
        return pack;
    }
}
