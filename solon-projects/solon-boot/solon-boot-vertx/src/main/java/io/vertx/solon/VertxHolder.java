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
import org.noear.solon.core.event.EventBus;

/**
 * Vertx 单例持有人
 *
 * @author noear
 * @since 3.0
 */
public class VertxHolder {
    private static Vertx vertx;

    public static Vertx getVertx() {
        if (vertx == null) {
            VertxOptions vertxOptions = new VertxOptions();
            //vertxOptions.setWorkerPoolSize(20);
            //vertxOptions.setEventLoopPoolSize(2 * Runtime.getRuntime().availableProcessors());
            //vertxOptions.setInternalBlockingPoolSize(20);

            //添加总线扩展
            EventBus.publish(vertxOptions);

            vertx = Vertx.vertx(vertxOptions);
        }

        return vertx;
    }

    public static void tryClose() {
        if (vertx != null) {
            vertx.close();
            vertx = null;
        }
    }
}
