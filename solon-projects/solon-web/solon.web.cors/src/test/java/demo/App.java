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
package demo;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.web.cors.CrossFilter;
import org.noear.solon.web.cors.CrossHandler;

/**
 * @author noear 2022/4/28 created
 */
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            //例：增加全局处理（用过滤器模式）
            app.filter(-1, new CrossFilter().allowedOrigins("*")); //加-1 优先级更高

            //例：增加全局处理（用处理链模式）
            app.before(new CrossHandler().allowedOrigins("*"));

            //例：或者增某段路径的处理
            app.before("/user/**", new CrossHandler().allowedOrigins("*"));
        });

        Solon.app().router().getAll(Endpoint.main).forEach(e -> {
            System.out.println(e.method() + " " + e.path());
        });
    }
}
