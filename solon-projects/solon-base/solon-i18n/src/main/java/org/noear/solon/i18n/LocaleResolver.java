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

import org.noear.solon.core.handle.Context;

import java.util.Locale;

/**
 * 地区解析器
 *
 * @author noear
 * @since 1.5
 */
public interface LocaleResolver {
    /**
     * 获取地区
     *
     * @param ctx 上下文
     * */
    Locale getLocale(Context ctx);

    /**
     * 设置地区
     *
     * @param ctx 上下文
     * @param locale 地区
     * */
    void setLocale(Context ctx, Locale locale);
}
