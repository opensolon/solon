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

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Pack;
import org.noear.solon.i18n.I18nBundle;
import org.noear.solon.i18n.I18nBundleFactory;

import java.util.Locale;

/**
 * @author noear
 * @since 1.6
 */
public class CloudI18nBundleFactory implements I18nBundleFactory {
    private final String group;


    public CloudI18nBundleFactory(){
        this(Solon.cfg().appGroup());
    }

    public CloudI18nBundleFactory(String group) {
        this.group = group;
    }

    @Override
    public I18nBundle create(String bundleName, Locale locale) {
        if (CloudClient.i18n() == null) {
            throw new IllegalStateException("Invalid CloudI18nService");
        }

        if ("i18n.messages".equals(bundleName)) {
            bundleName = null; //null is def
        }

        Pack pack = CloudClient.i18n().pull(group, bundleName, locale);
        return new CloudI18nBundle(pack, locale);
    }
}
