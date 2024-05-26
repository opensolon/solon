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

        if (path.contains("*") || path.contains("{")) {
            inner.add(routing);
        } else {
            inner.add(0, routing);
        }
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
