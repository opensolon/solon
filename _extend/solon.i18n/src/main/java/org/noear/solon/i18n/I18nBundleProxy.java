package org.noear.solon.i18n;

import org.noear.solon.annotation.Singleton;

import java.util.Map;

/**
 * @author noear
 * @since 1.5
 */
@Singleton(false)
public class I18nBundleProxy implements I18nBundle {
    I18nBundle bundle;

    public I18nBundleProxy() {
        bundle = I18nUtil.getMessageBundle();
    }

    @Override
    public Map<String, String> toMap() {
        return bundle.toMap();
    }

    @Override
    public String get(String code) {
        return bundle.get(code);
    }

    @Override
    public String getAndFormat(String code, Object... args) {
        return bundle.getAndFormat(code, args);
    }
}
