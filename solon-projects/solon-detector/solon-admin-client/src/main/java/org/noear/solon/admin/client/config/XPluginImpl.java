/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.admin.client.config;

import org.noear.solon.Solon;
import org.noear.solon.admin.client.annotation.EnableAdminClient;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.health.detector.DetectorManager;

/**
 * @author noear
 * @since 2.3
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        if(Solon.app().source().isAnnotationPresent(EnableAdminClient.class) == false){
            return;
        }

        context.beanScan("org.noear.solon.admin.client");

        EventBus.subscribe(AppLoadEndEvent.class, e->{
            DetectorManager.start("*");
        });
    }
}
