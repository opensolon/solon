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
package org.noear.solon.net.http.impl.okhttp;

import okhttp3.Dispatcher;
import org.noear.solon.Utils;

/**
 * Http 调度加载器（用于懒加载）
 *
 * @author noear
 * @since 3.4
 */
public class OkHttpDispatcherLoader {
    private Dispatcher dispatcher;

    public Dispatcher getDispatcher() {
        if (dispatcher == null) {
            Utils.locker().lock();
            try {
                if (dispatcher == null) {
                    dispatcher = new Dispatcher();
                    dispatcher.setMaxRequests(Integer.MAX_VALUE);
                    dispatcher.setMaxRequestsPerHost(10000);
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return dispatcher;
    }
}
