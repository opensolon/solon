package org.noear.solon.i18n.impl;

import org.noear.solon.Utils;
import org.noear.solon.i18n.I18nBundle;

import java.util.*;

/**
 * 国际化内容包，本地实现
 *
 * @author noear
 * @since 1.5
 */
public class I18nBundleLocal implements I18nBundle {
    Properties bundle;
    String bundleName;
    Locale locale;
    Map<String, String> map;

    public I18nBundleLocal(String bundleName, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        this.locale = locale;
        this.bundleName = bundleName;

        String bundleName2 = bundleName.replace(".", "/");

        this.bundle = Utils.loadProperties(bundleName2 + "_" + locale.toString() + ".properties");
        if (this.bundle == null) {
            this.bundle = Utils.loadProperties(bundleName2 + ".properties");
        }
    }

    /**
     * 转换为Map数据
     */
    @Override
    public synchronized Map<String, String> toMap() {
        if (map == null) {
            map = new LinkedHashMap<>();

            for (Object k : bundle.keySet()) {
                String key = (String) k;
                map.put(key, get(key));
            }
        }

        return map;
    }

    @Override
    public Locale locale() {
        return locale;
    }

    /**
     * 获取国际化内容
     *
     * @param key 配置键
     */
    @Override
    public String get(String key) {
        String tmp = bundle.getProperty(key);

        if (tmp == null) {
            throw new MissingResourceException("Can't find resource for bundle "
                    + bundleName
                    + ", key " + key,
                    this.getClass().getName(),
                    key);
        }

        return tmp;
    }
}
