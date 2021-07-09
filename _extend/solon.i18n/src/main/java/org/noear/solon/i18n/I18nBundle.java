package org.noear.solon.i18n;

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
     */
    Map<String, String> toMap();

    /**
     * 当前地区
     * */
    Locale locale();

    /**
     * 获取国际化内容
     *
     * @param name 配置名
     */
    String get(String name);

    /**
     * 获取国际化内容并格式化
     *
     * @param name 配置名
     * @param args 参数
     */
    default String getAndFormat(String name, Object... args) {
        String tml = get(name);

        MessageFormat mf = new MessageFormat(tml);
        if (locale() != null) {
            mf.setLocale(locale());
        }

        return mf.format(args);
    }
}
