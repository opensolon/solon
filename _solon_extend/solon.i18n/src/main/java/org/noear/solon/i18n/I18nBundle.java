package org.noear.solon.i18n;

import org.noear.solon.core.Props;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

/**
 * 国际化内容包
 *
 * @author noear
 * @since 1.5
 */
public interface I18nBundle {

    /**
     * 转换为Map数据
     * @deprecated 1.10
     */
    @Deprecated
    Map<String, String> toMap();

    /**
     * 转换为Props数据
     */
    default Props toProps(){
        Props tmp = new Props();
        tmp.putAll(toMap());
        return tmp;
    }

    /**
     * 当前地区
     * */
    Locale locale();

    /**
     * 获取国际化内容
     *
     * @param key 配置键
     */
    String get(String key);

    /**
     * 获取国际化内容并格式化
     *
     * @param key 配置键
     * @param args 参数
     */
    default String getAndFormat(String key, Object... args) {
        String tml = get(key);

        MessageFormat mf = new MessageFormat(tml);
        if (locale() != null) {
            mf.setLocale(locale());
        }

        return mf.format(args);
    }
}
