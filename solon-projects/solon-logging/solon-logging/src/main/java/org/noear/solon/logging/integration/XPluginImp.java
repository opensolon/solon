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
package org.noear.solon.logging.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.logging.LogOptions;
import org.noear.solon.logging.event.Appender;
import org.slf4j.MDC;

import java.util.Properties;


/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    public XPluginImp() {
        AppenderManager.init();
    }

    @Override
    public void start(AppContext context) {
        Properties props = Solon.cfg().getProp("solon.logging.appender");

        //初始化
        AppenderManager.init();

        //注册添加器
        if (props.size() > 0) {
            props.forEach((k, v) -> {
                String key = (String) k;
                String val = (String) v;

                if (key.endsWith(".class")) {
                    Appender appender = ClassUtil.tryInstance(val);
                    if (appender != null) {
                        String name = key.substring(0, key.length() - 6);
                        AppenderManager.register(name, appender);
                    }
                }
            });
        }

        //init
        LogOptions.getLoggerLevelInit();

        Solon.app().filter(Integer.MIN_VALUE, (ctx, chain) -> {
            if (ctx.path() != null && ctx.path().equals(ctx.pathNew())) {
                //避免 forward 时清掉 mdc
                MDC.clear();
            }

            chain.doFilter(ctx);
        });
    }

    @Override
    public void stop() throws Throwable {
        AppenderManager.stop();
    }
}