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
 * 国际化内容包
 *
 * @author noear
 * @since 1.5
 */
public interface I18nBundle {

    /**
     * 转换为Props数据
     */
    Props toProps();

    /**
     * 当前地区
     */
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
     * @param key  配置键
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
