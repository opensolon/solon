/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.i18n;

import org.noear.solon.core.Props;

import java.text.MessageFormat;
import java.util.Locale;

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
