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
package org.noear.solon.core.route;

import org.noear.solon.Solon;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.*;

import java.util.*;
import java.util.function.Predicate;

/**
 * 通用路由器
 *
 * <pre>{@code
 * public class DemoApp{
 *     public static void main(String[] args){
 *         Solon.start(DemoApp.class, args,app->{
 *             //
 *             //路由手写模式
 *             //
 *             app.router().get("/hello/*",c->coutput("heollo world;"));
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
 * }</pre>
 *
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public interface Router {
    /**
     * 区分大小写（默认区分）
     *
     * @param caseSensitive 区分大小写
     */
    void caseSensitive(boolean caseSensitive);

    /**
     * 添加路径前缀
     */
    void addPathPrefix(String pathPrefix, Predicate<Class<?>> tester);

    /**
     * 添加路由关系 for Handler
     *
     * @param path    路径
     * @param method  方法
     * @param index   顺序位
     * @param handler 处理接口
     */
    void add(String path, MethodType method, int index, Handler handler);

    /**
     * 添加路由关系 for Handler
     *
     * @param pathPrefix 路径前缀
     * @param bw         Bean 包装
     * @param remoting   是否为远程处理
     */
    void add(String pathPrefix, BeanWrap bw, boolean remoting);

    /**
     * 区配一个主处理，并获取状态（根据上下文）
     *
     * @param ctx 上下文
     * @return 一个匹配的处理结果
     */
    Result<Handler> matchMainAndStatus(Context ctx);

    /**
     * 区配一个主处理（根据上下文）
     *
     * @param ctx 上下文
     * @return 一个匹配的处理
     */
    Handler matchMain(Context ctx);


    /// /////////////////////////


    /**
     * 获取某个处理点的所有路由记录（管理用）
     *
     * @return 处理点的所有路由记录
     */
    Collection<Routing<Handler>> findAll();

    /**
     * 获取某个路径的某个处理点的路由记录（管理用）
     *
     * @param pathPrefix 路径前缀
     * @return 路径处理点的路由记录
     * @since 2.6
     */
    Collection<Routing<Handler>> findBy(String pathPrefix);

    /**
     * 获取某个控制器的路由记录（管理用）
     *
     * @param controllerClz 控制器类
     * @return 控制器处理点的路由记录
     */
    Collection<Routing<Handler>> findBy(Class<?> controllerClz);


    /**
     * 移除路由关系
     *
     * @param pathPrefix 路径前缀
     */
    void remove(String pathPrefix);

    /**
     * 移除路由关系
     *
     * @param controllerClz 控制器类
     */
    void remove(Class<?> controllerClz);

    /**
     * 清空路由关系
     */
    void clear();

    //--------------- ext0

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param index  顺序位
     * @param filter 过滤器
     * @since 1.5
     * @since 3.7
     */
    void filter(int index, Filter filter);

    /**
     * 添加过滤器（按先进后出策略执行）,如果有相同类的则不加
     *
     * @param index  顺序位
     * @param filter 过滤器
     * @since 2.6
     * @since 3.7
     */
    void filterIfAbsent(int index, Filter filter);

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param filter 过滤器
     * @since 3.7
     */
    default void filter(Filter filter) {
        filter(0, filter);
    }

    /**
     * 添加路由拦截器（按先进后出策略执行）
     *
     * @param index       顺序位
     * @param interceptor 路由拦截器
     * @since 3.7
     */
    void routerInterceptor(int index, RouterInterceptor interceptor);

    /**
     * 添加路由拦截器（按先进后出策略执行）,如果有相同类的则不加
     *
     * @param index       顺序位
     * @param interceptor 路由拦截器
     * @since 3.7
     */
    void routerInterceptorIfAbsent(int index, RouterInterceptor interceptor);

    /**
     * 添加路由拦截器（按先进后出策略执行）
     *
     * @param interceptor 路由拦截器
     * @since 3.7
     */
    default void routerInterceptor(RouterInterceptor interceptor) {
        routerInterceptor(0, interceptor);
    }

    //--------------- ext1

    /**
     * 添加路由关系 for Handler
     *
     * @param path    路径
     * @param handler 处理接口
     */
    default void add(String path, Handler handler) {
        add(path, MethodType.HTTP, handler);
    }

    /**
     * 添加路由关系 for Handler
     *
     * @param path    路径
     * @param method  方法
     * @param handler 处理接口
     */
    default void add(String path, MethodType method, Handler handler) {
        add(path, method, 0, handler);
    }

    /**
     * 添加路由关系 for Handler
     *
     * @param bw Bean 包装
     */
    default void add(BeanWrap bw) {
        add(null, bw);
    }

    /**
     * 添加路由关系 for Handler
     *
     * @param pathPrefix 路径前缀
     * @param bw         Bean 包装
     */
    default void add(String pathPrefix, BeanWrap bw) {
        add(pathPrefix, bw, bw.remoting());
    }


    //--------------- ext2

    /**
     * 添加主体处理
     *
     * @since 3.7
     */
    default void add(Class<?> clz) {
        BeanWrap bw = Solon.context().wrapAndPut(clz);
        add(null, bw);
    }

    /**
     * 添加主体处理
     */
    default void add(String pathPrefix, Class<?> clz) {
        BeanWrap bw = Solon.context().wrapAndPut(clz);
        add(pathPrefix, bw);
    }

    /**
     * 添加主体处理
     */
    default void add(String pathPrefix, Class<?> clz, boolean remoting) {
        BeanWrap bw = Solon.context().wrapAndPut(clz);
        add(pathPrefix, bw, remoting);
    }


    /**
     * 添加所有方法处理
     */
    default void all(String path, Handler handler) {
        add(path, MethodType.ALL, handler);
    }

    /**
     * 添加HTTP所有方法的处理（GET,POST,PUT,PATCH,DELETE,HEAD）
     */
    default void http(String path, Handler handler) {
        add(path, MethodType.HTTP, handler);
    }

    /**
     * 添加HEAD方法的处理
     */
    default void head(String path, Handler handler) {
        add(path, MethodType.HEAD, handler);
    }

    /**
     * 添加GET方法的处理（REST.select 从服务端获取一或多项资源）
     */
    default void get(String path, Handler handler) {
        add(path, MethodType.GET, handler);
    }

    /**
     * 添加POST方法的处理（REST.create 在服务端新建一项资源）
     */
    default void post(String path, Handler handler) {
        add(path, MethodType.POST, handler);
    }

    /**
     * 添加PUT方法的处理（REST.update 客户端提供改变后的完整资源）
     */
    default void put(String path, Handler handler) {
        add(path, MethodType.PUT, handler);
    }

    /**
     * 添加PATCH方法的处理（REST.update 客户端提供改变的属性）
     */
    default void patch(String path, Handler handler) {
        add(path, MethodType.PATCH, handler);
    }

    /**
     * 添加DELETE方法的处理（REST.delete 从服务端删除资源）
     */
    default void delete(String path, Handler handler) {
        add(path, MethodType.DELETE, handler);
    }


    /**
     * 添加socket方法的监听
     */
    default void socketd(String path, Handler handler) {
        add(path, MethodType.SOCKET, handler);
    }


    //------------- Deprecated


    /**
     * 获取某个处理点的所有路由记录（管理用）
     *
     * @return 处理点的所有路由记录
     * @deprecated 3.7 避免与 get(path,hander) 疑似冲突 {@link #findAll()}
     */
    @Deprecated
    default Collection<Routing<Handler>> getAll() {
        return findAll();
    }

    /**
     * 获取某个路径的某个处理点的路由记录（管理用）
     *
     * @param pathPrefix 路径前缀
     * @return 路径处理点的路由记录
     * @since 2.6
     * @deprecated 3.7 避免与 get(path,hander) 疑似冲突 {@link #findBy(String)}
     */
    @Deprecated
    default Collection<Routing<Handler>> getBy(String pathPrefix) {
        return findBy(pathPrefix);
    }

    /**
     * 获取某个控制器的路由记录（管理用）
     *
     * @param controllerClz 控制器类
     * @return 控制器处理点的路由记录
     * @deprecated 3.7 避免与 get(path,hander) 疑似冲突 {@link #findBy(Class)}
     */
    @Deprecated
    default Collection<Routing<Handler>> getBy(Class<?> controllerClz) {
        return findBy(controllerClz);
    }
}