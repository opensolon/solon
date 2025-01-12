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
package org.noear.solon.health.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.health.HealthChecker;
import org.noear.solon.health.HealthHandler;
import org.noear.solon.health.HealthIndicator;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class HealthPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        //
        // HealthHandler 独立出来，便于其它检测路径的复用
        //
        context.app().get(HealthHandler.HANDLER_PATH, HealthHandler.getInstance());
        context.app().head(HealthHandler.HANDLER_PATH, HealthHandler.getInstance());

        //添加 HealthIndicator 自动注册
        context.subWrapsOfType(HealthIndicator.class, bw -> {
            if (Utils.isEmpty(bw.name())) {
                //使用类名作指标名
                HealthChecker.addIndicator(bw.clz().getSimpleName(), bw.get());
            } else {
                HealthChecker.addIndicator(bw.name(), bw.get());
            }
        });
    }
}