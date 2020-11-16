package org.noear.solon.core.route;

import org.noear.solon.core.handler.Endpoint;
import org.noear.solon.core.message.MessageListener;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.core.handler.MethodType;
import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.Handler;

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
    private final RouteTable<MessageListener> routesL;

    public Router() {
        routesH = new RouteTable[3];

        routesH[0] = new RouteTable<>();//before:0
        routesH[1] = new RouteTable<>();//main
        routesH[2] = new RouteTable<>();//after:2

        routesL = new RouteTable<>();
    }

    /**
     * 添加路由关系 for XHandler
     */
    public void add(String path, Handler handler) {
        add(path, Endpoint.main, MethodType.HTTP, handler);
    }

    /**
     * 添加路由关系 for XHandler
     */
    public void add(String path, Endpoint endpoint, MethodType method, Handler handler) {
        add(path, endpoint, method, 0, handler);
    }

    /**
     * 添加路由关系 for XHandler
     */
    public void add(String path, Endpoint endpoint, MethodType method, int index, Handler handler) {
        routesH[endpoint.code].add(new RouteTable.Route(path, method, index, handler));
    }

    /**
     * 添加路由关系 for XListener
     */
    public void add(String path, MessageListener listener) {
        add(path, MethodType.ALL, listener);
    }

    public void add(String path, MethodType method, MessageListener listener) {
        add(path, method, 0, listener);
    }

    /**
     * 添加路由关系 for XListener
     */
    public void add(String path, MethodType method, int index, MessageListener listener) {
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
    public MessageListener matchOne(MessageSession session) {
        String path = session.path();

        return routesL.matchOne(path, session.method());
    }
}
