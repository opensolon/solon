package org.noear.solon.i18n.impl;

import org.noear.solon.i18n.I18nBundle;
import org.noear.solon.i18n.I18nBundleFactory;

import java.util.Locale;

/**
 * 国际化内容包工厂，本地实现
 *
 * @author noear
 * @since 1.5
 */
public class I18nBundleFactoryLocal implements I18nBundleFactory {
    @Override
    public I18nBundle create(String bundleName, Locale locale) {
        return new I18nBundleLocal(bundleName, locale);
    }
}
