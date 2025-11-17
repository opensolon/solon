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
package io.vertx.solon;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.event.EventBus;

/**
 * Vertx 持有管理
 *
 * @author noear
 * @since 3.7
 */
public class VertxHolder {
    public static Vertx getVertx(AppContext context) {
        Vertx vertx = context.getBean(Vertx.class);

        if (vertx == null) {
            VertxOptions vertxOptions = new VertxOptions();
            EventBus.publish(vertxOptions); //添加总线扩展

            vertx = Vertx.vertx(vertxOptions);
            context.wrapAndPut(Vertx.class, vertx);
            context.lifecycle(new VertxLifecycle(vertx));
        }

        return vertx;
    }
}
