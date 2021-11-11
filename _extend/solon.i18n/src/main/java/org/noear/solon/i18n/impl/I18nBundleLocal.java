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
            locale = Solon.cfg().locale();
        }

        this.locale = locale;
        this.bundleName = bundleName;

        String bundleName2 = bundleName.replace(".", "/");

        //加载默认配置
        bundle = Utils.loadProperties(bundleName2 + ".properties");
        if (bundle == null) {
            bundle = new Properties();
        }

        //加载地区配置
        Properties tmp = null;

        //尝试加(语言)的配置
        tmp = Utils.loadProperties(bundleName2 + "_" + locale.getLanguage() + ".properties");

        if (tmp != null) {
            //如果有，替换掉默认配置
            tmp.forEach((k, v) -> {
                bundle.put(k, v);
            });
        }

        //尝试(语言_国家)的配置
        tmp = Utils.loadProperties(bundleName2 + "_" + locale + ".properties");

        if (tmp != null) {
            //如果有，替换掉默认配置
            tmp.forEach((k, v) -> {
                bundle.put(k, v);
            });
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
