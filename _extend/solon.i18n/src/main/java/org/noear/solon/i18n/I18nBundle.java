package org.noear.solon.i18n;

import java.util.Map;

/**
 * 国际化包
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
     * 获取国际化内容
     *
     * @param name 名称
     */
    String get(String name);

    /**
     * 获取国际化内容
     *
     * @param name 名称
     * @param args 参数
     */
    String getAndFormat(String name, Object... args);
}
