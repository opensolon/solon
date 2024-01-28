package org.noear.solon.core.route;

import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.PathAnalyzer;

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
     * 区分大小写（默认不区分）
     */
    default void caseSensitive(boolean caseSensitive) {
        PathAnalyzer.setCaseSensitive(caseSensitive);
    }

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
     * 获取某个路径的某个处理点的路由记录
     *
     * @since 2.6
     * @param path     路径
     * @param endpoint 处理点
     * @return 路径处理点的路由记录
     */
    Collection<Routing<Handler>> getBy(String path, Endpoint endpoint);


    /**
     * 区配一个处理（根据上下文）
     *
     * @param ctx      上下文
     * @param endpoint 处理点
     * @return 一个匹配的处理
     */
    Handler matchOne(Context ctx, Endpoint endpoint);

    /**
     * 区配主处理（根据上下文）
     *
     * @param ctx 上下文
     * @return 一个匹配的处理
     */
    Handler matchMain(Context ctx);

    /**
     * 区配多个处理（根据上下文）
     *
     * @param ctx      上下文
     * @param endpoint 处理点
     * @return 一批匹配的处理
     * @since 2.5
     */
    List<Handler> matchMore(Context ctx, Endpoint endpoint);

    /**
     * 清空路由关系
     */
    void clear();
}
