package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.i18n.I18nBundle;
import org.noear.solon.i18n.I18nBundleFactory;

import java.util.Locale;
import java.util.Map;

/**
 * @author noear
 * @since 1.6
 */
public class CloudI18nBundleFactory implements I18nBundleFactory {
    private String group;

    public CloudI18nBundleFactory(String group) {
        this.group = group;
    }

    @Override
    public I18nBundle create(String bundleName, Locale locale) {
        if (CloudClient.i18n() == null) {
            throw new IllegalStateException("Invalid CloudI18nService");
        }

        if ("i18n.messages".equals(bundleName)) {
            bundleName = Solon.cfg().appName();
        }

        Map<String, String> data = CloudClient.i18n().pull(group, bundleName, locale);
        return new CloudI18nBundle(data, locale);
    }
}
