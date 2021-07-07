package org.noear.solon.i18n;

import java.util.Locale;

/**
 * 国际化包工厂，本地实现
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
