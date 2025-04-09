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

import org.noear.solon.Utils;

import java.util.Locale;

/**
 * 地区转换工具
 *
 * <pre>{@code
 * LocaleUtil.toLocale("zh") == new Locale("zh", "")
 * LocaleUtil.toLocale("zh_Hant") == new Locale("zh", "Hant")
 * LocaleUtil.toLocale("zh_Hant_TW") == new Locale("zh", "Hant", "TW")
 * }</pre>
 *
 * @author noear
 * @since 1.5
 */
public class LocaleUtil {
    /**
     * 将字符串转换成地区
     *
     * @param lang 语言字符串
     */
    public static Locale toLocale(String lang) {
        return Utils.toLocale(lang);
    }
}
