package org.noear.solon.i18n.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Props;
import org.noear.solon.i18n.I18nBundle;

import java.util.*;

/**
 * 国际化内容包，本地实现
 *
 * @author noear
 * @since 1.5
 */
public class I18nBundleLocal implements I18nBundle {
    Props bundle = new Props();
    String bundleName;
    Locale locale;

    public I18nBundleLocal(String bundleName, Locale locale) {
        if (locale == null) {
            locale = Solon.cfg().locale();
        }

        this.locale = locale;
        this.bundleName = bundleName;

        String bundleName2 = bundleName.replace(".", "/");

        //加载默认配置
        Properties tmp = loadProperties(bundleName2, new String[]{".properties", ".yml"});
        if (tmp != null) {
            //如果有，替换掉默认配置
            bundle.putAll(tmp);
        }

        //加载地区配置

        //尝试加(语言)的配置
        tmp = loadProperties(bundleName2 + "_" + locale.getLanguage(), new String[]{".properties", ".yml"});
        if (tmp != null) {
            //如果有，替换掉默认配置
            bundle.putAll(tmp);
        }

        //尝试(语言_国家)的配置
        tmp = loadProperties(bundleName2 + "_" + locale, new String[]{".properties", ".yml"});
        if (tmp != null) {
            //如果有，替换掉默认配置
            bundle.putAll(tmp);
        }
    }

    /**
     * 转换为Map数据
     */
    @Override
    public synchronized Map<String, String> toMap() {
        return (Map)bundle;
    }

    @Override
    public Props toProps() {
        return bundle;
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

    private Properties loadProperties(String path, String[] extensions) {
        Properties tmp = null;
        for (String ext : extensions) {
            tmp = Utils.loadProperties(path + ext);
            if (tmp != null) {
                break;
            }
        }

        return tmp;
    }
}
