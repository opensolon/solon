package org.noear.solon.i18n;

import java.util.Locale;

/**
 * 国际化内容包工厂
 *
 * @author noear
 * @since 1.5
 */
public interface I18nBundleFactory {
    /**
     * 创建国际化包
     * */
    I18nBundle create(String bundleName, Locale locale);
}
