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
package org.noear.solon.net.socketd.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.listener.RouteSelector;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.Routing;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.core.route.RoutingTable;
import org.noear.solon.core.route.RoutingTableDefault;

/**
 * 表达式路由器
 *
 * @author noear
 * @since 2.0
 */
public class RouteSelectorExpress implements RouteSelector<Listener> {

    private final RoutingTable<Listener> inner = new RoutingTableDefault<>();

    @Override
    public Listener select(String path) {
        return inner.matchOne(path, MethodType.SOCKET);
    }

    @Override
    public void put(String path, Listener listener) {
        Routing<Listener> routing = new RoutingDefault<>(path, MethodType.SOCKET, 0, listener);

        inner.add(routing);
    }

    @Override
    public void remove(String path) {
        inner.remove(path);
    }

    @Override
    public int size() {
        return inner.count();
    }
}
