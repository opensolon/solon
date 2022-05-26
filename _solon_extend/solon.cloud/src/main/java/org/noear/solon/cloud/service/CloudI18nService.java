package org.noear.solon.cloud.service;

import org.noear.solon.cloud.model.Pack;

import java.util.Locale;

/**
 * 云端国际化服务
 *
 * @author noear
 * @since  1.6
 */
public interface CloudI18nService {
    /**
     * 拉取语言包
     *
     * @param group    分组
     * @param packName 包名
     * @param locale   地区
     */
    Pack pull(String group, String packName, Locale locale);
}
