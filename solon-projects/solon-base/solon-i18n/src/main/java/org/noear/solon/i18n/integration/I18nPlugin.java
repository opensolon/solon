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
package org.noear.solon.i18n.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Constants;
import org.noear.solon.core.Plugin;
import org.noear.solon.i18n.I18nBundleFactory;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.i18n.LocaleResolver;
import org.noear.solon.i18n.annotation.I18n;

/**
 * @author noear
 */
public class I18nPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        context.beanInterceptorAdd(I18n.class, I18nInterceptor.instance);

        context.getBeanAsync(I18nBundleFactory.class, I18nUtil::setBundleFactory);

        context.getBeanAsync(LocaleResolver.class, I18nUtil::setLocaleResolver);

        context.app().filter(Constants.FT_IDX_I18N, new I18nFilter());
    }
}
