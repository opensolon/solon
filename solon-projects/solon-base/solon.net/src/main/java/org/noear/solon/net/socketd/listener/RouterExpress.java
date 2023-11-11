package org.noear.solon.net.socketd.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.listener.Router;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.core.route.RoutingTable;
import org.noear.solon.core.route.RoutingTableDefault;

/**
 * 表达式路由器
 *
 * @author noear
 * @since 2.0
 */
public class RouterExpress implements Router {

    private final RoutingTable<Listener> table = new RoutingTableDefault<>();
    @Override
    public Listener matching(String path) {
        return table.matchOne(path, MethodType.SOCKET);
    }

    @Override
    public void add(String path, Listener listener) {
        table.add(new RoutingDefault<>(path, MethodType.SOCKET, 0, listener));
    }

    @Override
    public void remove(String path) {
         table.remove(path);
    }

    @Override
    public int count() {
        return table.count();
    }
}
