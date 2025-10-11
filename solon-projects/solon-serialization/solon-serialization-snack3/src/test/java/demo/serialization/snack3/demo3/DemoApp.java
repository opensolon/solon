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
package demo.serialization.snack3.demo3;

import org.noear.solon.Solon;
import org.noear.solon.serialization.snack3.SnackActionExecutor;

/**
 * @author noear 2022/10/31 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(demo.serialization.snack3.demo2.DemoApp.class, args, app -> {
            app.onEvent(SnackActionExecutor.class, executor -> {
                executor.config().addDecoder(String.class, (node, type) -> {
                    return node.getString();
                });
            });
        });
    }
}
