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
package ch.qos.logback.solon.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

import java.net.URL;

/**
 * @author noear
 * @since 1.6
 */
public class LogbackPlugin extends LogIncubatorImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        //容器加载完后，允许再次处理
        incubate();
    }

    @Override
    protected void doLoadUrl(URL url) throws Exception {
        if (url == null) {
            //说明没有指定配置文件。。。不需要再次默认加载
            return;
        }

        super.doLoadUrl(url);
    }
}