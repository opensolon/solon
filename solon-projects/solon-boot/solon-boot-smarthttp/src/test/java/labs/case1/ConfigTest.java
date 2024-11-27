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
package labs.case1;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.boot.http.HttpServerConfigure;

/**
 * @author noear 2023/5/13 created
 */
@SolonMain
public class ConfigTest {
    public static void main(String[] args) {
        Solon.start(ConfigTest.class, args, app -> {
            app.onEvent(HttpServerConfigure.class, e -> {
                e.enableDebug(true);
            });
        });
    }
}
