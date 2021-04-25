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
public interface Router {

    /**
     * 添加路由关系 for Handler
     */
    default void add(String path, Handler handler) {
        add(path, Endpoint.main, MethodType.HTTP, handler);
    }

    /**
     * 添加路由关系 for Handler
     */
    default void add(String path, Endpoint endpoint, MethodType method, Handler handler) {
        add(path, endpoint, method, 0, handler);
    }

    /**
     * 添加路由关系 for Handler
     */
    void add(String path, Endpoint endpoint, MethodType method, int index, Handler handler);

    /**
     * 添加路由关系 for XListener
     */
    default void add(String path, Listener listener) {
        add(path, MethodType.ALL, listener);
    }

    default void add(String path, MethodType method, Listener listener) {
        add(path, method, 0, listener);
    }

    /**
     * 添加路由关系 for XListener
     */
    void add(String path, MethodType method, int index, Listener listener);


    List<Routing<Handler>> getItems(Endpoint endpoint);

    /**
     * 区配一个目标（根据上上文）
     */
    Handler matchOne(Context context, Endpoint endpoint);

    /**
     * 区配多个目标（根据上上文）
     */
    List<Handler> matchAll(Context context, Endpoint endpoint);

    /**
     * 区配一个目标（根据上上文）
     */
    Listener matchOne(Session session);


    /**
     * 清空路由关系
     */
    void clear();
}
