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
package org.noear.solon.web.stop;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        boolean enable = context.cfg().getBool("solon.stop.enable", false);
        String path = context.cfg().get("solon.stop.path", "/_run/stop/");
        String whitelist = context.cfg().get("solon.stop.whitelist", "127.0.0.1");

        //单位为秒
        int delay = context.cfg().getInt("solon.stop.delay", 10);

        //开启WEB关闭
        if (enable) {
            context.app().get(path, (c) -> {
                int delay2 = c.paramAsInt("delay", delay);

                if (delay2 < 0) {
                    delay2 = 0;
                }

                if ("*".equals(whitelist)) {
                    Solon.stop(delay2);
                } else if (whitelist.equals(c.realIp())) {
                    Solon.stop(delay2);
                }
            });
        }
    }
}
