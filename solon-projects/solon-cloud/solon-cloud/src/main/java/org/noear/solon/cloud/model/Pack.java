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
package org.noear.solon.cloud.model;

import org.noear.solon.core.Props;

import java.util.Locale;

/**
 * 数据包
 *
 * @author noear
 * @since 1.6
 */
public class Pack {
    private final Locale locale;
    private final String lang;

    private Props data;

    public Pack(Locale locale) {
        this.locale = locale;
        this.lang = locale.toString();
    }

    public void setData(Props data) {
        this.data = data;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLang() {
        return lang;
    }

    public Props getData() {
        return data;
    }
}
