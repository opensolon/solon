/*
 * Copyright 2017-2024 noear.org and authors
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

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.*;

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
 * @since 3.0
 * */
public interface Router {

    /**
     * 区分大小写（默认不区分）
     *
     * @param caseSensitive 区分大小写
     */
    void caseSensitive(boolean caseSensitive);

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
     * @param path    路径
     * @param method  方法
     * @param index   顺序位
     * @param handler 处理接口
     */
    void add(String path, MethodType method, int index, Handler handler);

    /**
     * 添加路由关系 for Handler
     *
     * @param controllerWrap 控制器包装
     */
    void add(BeanWrap controllerWrap);

    /**
     * 添加路由关系 for Handler
     *
     * @param path           路径
     * @param controllerWrap 控制器包装
     */
    void add(String path, BeanWrap controllerWrap);

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

    /**
     * 获取某个处理点的所有路由记录（管理用）
     *
     * @return 处理点的所有路由记录
     */
    Collection<Routing<Handler>> getAll();

    /**
     * 获取某个路径的某个处理点的路由记录（管理用）
     *
     * @param path 路径
     * @return 路径处理点的路由记录
     * @since 2.6
     */
    Collection<Routing<Handler>> getBy(String path);

    /**
     * 获取某个控制器的路由记录（管理用）
     *
     * @param controllerClz 控制器类
     * @return 控制器处理点的路由记录
     */
    Collection<Routing<Handler>> getBy(Class<?> controllerClz);


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
}