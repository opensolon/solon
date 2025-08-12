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
package demo.boot.undertow;

import org.noear.solon.Solon;
import org.noear.solon.boot.http.HttpServerConfigure;

/**
 * @author noear 2023/5/11 created
 */
public class SeverDemo {
    public static void main(String[] args) {
        Solon.start(SeverDemo.class, args, app -> {
            if (app.cfg().argx().containsKey("ssl")) {
                //如果有 https ，就再加个 http
                app.onEvent(HttpServerConfigure.class, e -> {
                    e.addHttpPort(8082);
                });
            }

            app.get("/", ctx -> {
                ctx.output("Hello World");
            });
        });
    }
}
