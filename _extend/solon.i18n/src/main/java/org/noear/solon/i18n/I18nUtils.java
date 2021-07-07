package org.noear.solon.i18n;

import java.util.*;

/**
 * @author noear
 * @since 1.5
 */
public class I18nUtils {
    private static I18nBundleFactory bundleFactory = new I18nBundleFactoryLocal();
    private static Map<String, I18nBundle> bundleCached = new HashMap<>();

    public static I18nBundle get(String bundleName, Locale locale) {
        String key = bundleName + locale.toString();

        I18nBundle bundle = bundleCached.get(key);
        if (bundle == null) {
            synchronized (key.intern()) {
                bundle = bundleCached.get(key);

                if (bundle == null) {
                    bundle = bundleFactory.create(bundleName, locale);
                    bundleCached.put(key, bundle);
                }
            }
        }

        return bundle;
    }
}
