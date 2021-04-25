package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Session;

import java.util.List;

/**
 * @author noear 2021/4/26 created
 */
public class RouterDefault implements Router{
    //for handler
    private final RoutingTable<Handler>[] routesH;
    //for listener
    private final RoutingTable<Listener> routesL;

    public RouterDefault() {
        routesH = new RoutingTable[3];

        routesH[0] = new RoutingTable<>();//before:0
        routesH[1] = new RoutingTable<>();//main
        routesH[2] = new RoutingTable<>();//after:2

        routesL = new RoutingTable<>();
    }

    /**
     * 添加路由关系 for Handler
     */
    public void add(String path, Handler handler) {
        add(path, Endpoint.main, MethodType.HTTP, handler);
    }

    /**
     * 添加路由关系 for Handler
     */
    public void add(String path, Endpoint endpoint, MethodType method, Handler handler) {
        add(path, endpoint, method, 0, handler);
    }

    /**
     * 添加路由关系 for Handler
     */
    public void add(String path, Endpoint endpoint, MethodType method, int index, Handler handler) {
        routesH[endpoint.code].add(new RoutingDefault<>(path, method, index, handler));
    }

    /**
     * 添加路由关系 for XListener
     */
    public void add(String path, Listener listener) {
        add(path, MethodType.ALL, listener);
    }

    public void add(String path, MethodType method, Listener listener) {
        add(path, method, 0, listener);
    }


    @Override
    public List<Routing<Handler>> getItems(Endpoint endpoint){
        return routesH[endpoint.code];
    }

    /**
     * 添加路由关系 for XListener
     */
    public void add(String path, MethodType method, int index, Listener listener) {
        routesL.add(new RoutingDefault<>(path, method, index, listener));
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public Handler matchOne(Context context, Endpoint endpoint) {
        String path = context.path();
        MethodType method = MethodType.valueOf(context.method());

        return routesH[endpoint.code].matchOne(path, method);
    }

    /**
     * 区配多个目标（根据上上文）
     */
    public List<Handler> matchAll(Context context, Endpoint endpoint) {
        String path = context.path();
        MethodType method = MethodType.valueOf(context.method());

        return routesH[endpoint.code].matchAll(path, method);
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public Listener matchOne(Session session) {
        String path = session.path();

        if (path == null) {
            return null;
        } else {
            return routesL.matchOne(path, session.method());
        }
    }



    /**
     * 清空路由关系
     */
    public void clear() {
        routesH[0].clear();
        routesH[1].clear();
        routesH[2].clear();

        routesL.clear();
    }
}
