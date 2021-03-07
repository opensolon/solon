package org.noear.solon.core.route;

import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.util.*;

/**
 * 通用路由器
 *
 * <pre><code>
 * public class DemoApp{
 *     public static void main(String[] args){
 *         Solon.start(DemoApp.class, args,app->{
 *             //
 *             //路由手写模式
 *             //
 *             app.get("/hello/*",c->coutput("heollo world;"));
 *         });
 *     }
 * }
 *
 * //
 * //容器自动模式
 * //
 * @Controller
 * public class HelloController{
 *     @Mapping("/hello/*")
 *     public String hello(){
 *         return "heollo world;";
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public class Router {
    //for handler
    private final RouteTable<Handler>[] routesH;
    //for listener
    private final RouteTable<Listener> routesL;

    public Router() {
        routesH = new RouteTable[3];

        routesH[0] = new RouteTable<>();//before:0
        routesH[1] = new RouteTable<>();//main
        routesH[2] = new RouteTable<>();//after:2

        routesL = new RouteTable<>();
    }

    public RouteTable<Handler> main() {
        return routesH[1];
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
        routesH[endpoint.code].add(new RouteTable.Route(path, method, index, handler));
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

    /**
     * 添加路由关系 for XListener
     */
    public void add(String path, MethodType method, int index, Listener listener) {
        routesL.add(new RouteTable.Route(path, method, index, listener));
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
}
