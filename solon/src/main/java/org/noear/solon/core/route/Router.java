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
     *
     * @param path    路径
     * @param handler 处理接口
     */
    default void add(String path, Handler handler) {
        add(path, Endpoint.main, MethodType.HTTP, handler);
    }

    /**
     * 添加路由关系 for Handler
     *
     * @param path     路径
     * @param endpoint 处理点
     * @param method   方法
     * @param handler  处理接口
     */
    default void add(String path, Endpoint endpoint, MethodType method, Handler handler) {
        add(path, endpoint, method, 0, handler);
    }

    /**
     * 添加路由关系 for Handler
     *
     * @param path     路径
     * @param endpoint 处理点
     * @param method   方法
     * @param index    顺序位
     * @param handler  处理接口
     */
    void add(String path, Endpoint endpoint, MethodType method, int index, Handler handler);

    void remove(String pathPrefix);

    /**
     * 获取某个处理点的所有路由记录
     *
     * @param endpoint 处理点
     * @return 处理点的所有路由记录
     */
    Collection<Routing<Handler>> getAll(Endpoint endpoint);


    /**
     * 区配一个处理（根据上下文）
     *
     * @param ctx      上下文
     * @param endpoint 处理点
     * @return 一个匹配的处理
     */
    Handler matchOne(Context ctx, Endpoint endpoint);

    default Handler matchMain(Context ctx) {
        //不能从缓存里取，不然 pathNew 会有问题
        Handler tmp = matchOne(ctx, Endpoint.main);
        if (tmp != null) {
            ctx.attrSet("_MainHandler", tmp);
        }

        return tmp;
    }

    /**
     * 区配多个处理（根据上下文）
     *
     * @param ctx      上下文
     * @param endpoint 处理点
     * @return 一批匹配的处理
     */
    List<Handler> matchAll(Context ctx, Endpoint endpoint);


    /////////////////// for Listener ///////////////////


    /**
     * 添加路由关系 for Listener
     *
     * @param path     路径
     * @param listener 监听接口
     */
    default void add(String path, Listener listener) {
        add(path, MethodType.ALL, listener);
    }


    /**
     * 添加路由关系 for Listener
     *
     * @param path     路径
     * @param method   方法
     * @param listener 监听接口
     */
    default void add(String path, MethodType method, Listener listener) {
        add(path, method, 0, listener);
    }

    /**
     * 添加路由关系 for Listener
     *
     * @param path     路径
     * @param method   方法
     * @param index    顺序位
     * @param listener 监听接口
     */
    void add(String path, MethodType method, int index, Listener listener);

    /**
     * 区配一个目标（会话对象）
     *
     * @param session 会话对象
     * @return 首个匹配监听
     */
    Listener matchOne(Session session);

    /**
     * 区配多个目标（会话对象）
     *
     * @param session 会话对象
     * @return 多个匹配监听
     */
    List<Listener> matchAll(Session session);


    /**
     * 清空路由关系
     */
    void clear();
}
