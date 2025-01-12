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
package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.model.Pack;
import org.noear.solon.core.Props;
import org.noear.solon.i18n.I18nBundle;

import java.util.Locale;

/**
 * @author noear
 * @since 1.6
 */
public class CloudI18nBundle implements I18nBundle {
    private Pack pack;
    private Locale locale;

    public CloudI18nBundle(Pack pack, Locale locale) {
        this.pack = pack;
        this.locale = locale;
    }

    @Override
    public Props toProps() {
        return pack.getData();
    }

    @Override
    public Locale locale() {
        return locale;
    }

    @Override
    public String get(String key) {
        return pack.getData().get(key);
    }
}
