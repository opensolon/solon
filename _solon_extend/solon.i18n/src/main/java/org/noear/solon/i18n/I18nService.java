package org.noear.solon.i18n;

import org.noear.solon.core.Props;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

/**
 * 国际化内容服务
 *
 * @author noear
 * @since 1.5
 */
public class I18nService {
    private final String bundleName;

    public I18nService(String bundleName) {
        this.bundleName = bundleName;
    }


    /**
     * 获取国际化内容包
     *
     * @param locale 地域与语言
     * */
    public I18nBundle getBundle(Locale locale) {
        return I18nUtil.getBundle(bundleName, locale);
    }

    /**
     * 转换为 Map 数据
     *
     * @param locale 地域与语言
     * @deprecated 1.10
     */
    @Deprecated
    public Map<String, String> toMap(Locale locale) {
        return getBundle(locale).toMap();
    }

    /**
     * 转换为 Props 数据
     *
     * @param locale 地域与语言
     */
    public Props toProps(Locale locale) {
        return getBundle(locale).toProps();
    }


    /**
     * 获取国际化内容
     *
     * @param locale 地域与语言
     * @param key 配置键
     */
    public String get(Locale locale, String key) {
        return getBundle(locale).get(key);
    }

    /**
     * 获取国际化内容并格式化
     *
     * @param locale 地域与语言
     * @param key  配置键
     * @param args 参数
     */
    public String getAndFormat(Locale locale, String key, Object... args) {
        String tml = get(locale, key);

        MessageFormat mf = new MessageFormat(tml);
        mf.setLocale(locale);

        return mf.format(args);
    }
}
