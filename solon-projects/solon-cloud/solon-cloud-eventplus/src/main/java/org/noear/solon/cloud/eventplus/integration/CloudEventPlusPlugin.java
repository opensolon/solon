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
package org.noear.solon.cloud.eventplus.integration;

import org.noear.solon.cloud.eventplus.CloudEventSubscribe;
import org.noear.solon.cloud.eventplus.impl.CloudEventSubscribeBeanBuilder;
import org.noear.solon.cloud.eventplus.impl.CloudEventSubscribeBeanExtractor;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class CloudEventPlusPlugin implements Plugin {

    @Override
    public void start(AppContext context) {
        context.beanBuilderAdd(CloudEventSubscribe.class,
                new CloudEventSubscribeBeanBuilder());

        context.beanExtractorAdd(CloudEventSubscribe.class,
                new CloudEventSubscribeBeanExtractor());
    }
}
