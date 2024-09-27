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

import org.noear.solon.Solon;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Constants;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.PathAnalyzer;

import java.util.*;

/**
 * @author noear
 * @since 1.3
 */
public class RouterDefault implements Router, HandlerSlots {
    //for handler
    private final RoutingTable<Handler>[] routesH;

    public RouterDefault() {
        routesH = new RoutingTableDefault[3];

        routesH[0] = new RoutingTableDefault<>();//before:0
        routesH[1] = new RoutingTableDefault<>();//main
        routesH[2] = new RoutingTableDefault<>();//after:2
    }

    @Override
    public void caseSensitive(boolean caseSensitive) {
        PathAnalyzer.setCaseSensitive(caseSensitive);
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
    @Override
    public void add(String path, Endpoint endpoint, MethodType method, int index, Handler handler) {
        RoutingDefault routing = new RoutingDefault<>(path, method, index, handler);

        if (path.contains("*") || path.contains("{")) {
            routesH[endpoint.code].add(routing);
        } else {
            //没有*号的，优先
            routesH[endpoint.code].add(0, routing);
        }
    }

    @Override
    public void add(BeanWrap controllerWrap) {
        if (controllerWrap != null) {
            ActionLoader al = Solon.app().factoryManager().mvcFactory()
                    .createLoader(controllerWrap);

            if (controllerWrap.remoting()) {
                if (al.mapping() == null) {
                    //如果没有 mapping，则不进行  remoting注册
                    return;
                }
            }

            al.load(this);
        }
    }

    @Override
    public void add(String path, BeanWrap controllerWrap) {
        if (controllerWrap != null) {
            Solon.app().factoryManager().mvcFactory()
                    .createLoader(controllerWrap, path, controllerWrap.remoting(), null, true)
                    .load(this);
        }
    }

    /**
     * 区配一个处理（根据上下文）
     *
     * @param ctx      上下文
     * @param endpoint 处理点
     * @return 一个匹配的处理
     * @deprecated 2.8
     */
    @Deprecated
    @Override
    public Handler matchOne(Context ctx, Endpoint endpoint) {
        String pathNew = ctx.pathNew();
        MethodType method = MethodTypeUtil.valueOf(ctx.method());

        return routesH[endpoint.code].matchOne(pathNew, method);
    }

    @Override
    public Handler matchMain(Context ctx) {
        //不能从缓存里取，不然 pathNew 会有问题
        String pathNew = ctx.pathNew();
        MethodType method = MethodTypeUtil.valueOf(ctx.method());

        Result<Handler> result = routesH[Endpoint.main.code].matchOneAndStatus(pathNew, method);

        if (result.getData() != null) {
            ctx.attrSet(Constants.mainHandler, result.getData());
            ctx.attrSet(Constants.mainStatus, 200);
        } else {
            ctx.attrSet(Constants.mainHandler, null);
            ctx.attrSet(Constants.mainStatus, result.getCode());
        }

        return result.getData();
    }

    /**
     * 区配多个处理（根据上下文）
     *
     * @param ctx      上下文
     * @param endpoint 处理点
     * @return 一批匹配的处理
     */
    @Override
    public List<Handler> matchMore(Context ctx, Endpoint endpoint) {
        String pathNew = ctx.pathNew();
        MethodType method = MethodTypeUtil.valueOf(ctx.method());

        return routesH[endpoint.code].matchMore(pathNew, method);
    }


    /**
     * 获取某个处理点的所有路由记录（管理用）
     *
     * @param endpoint 处理点
     * @return 处理点的所有路由记录
     */
    @Override
    public Collection<Routing<Handler>> getAll(Endpoint endpoint) {
        return routesH[endpoint.code].getAll();
    }

    /**
     * 获取某个路径的某个处理点的路由记录（管理用）
     *
     * @param path     路径
     * @param endpoint 处理点
     * @return 路径处理点的路由记录
     * @since 2.6
     */
    @Override
    public Collection<Routing<Handler>> getBy(String path, Endpoint endpoint) {
        return routesH[endpoint.code].getBy(path);
    }

    @Override
    public Collection<Routing<Handler>> getBy(Class<?> controllerClz, Endpoint endpoint) {
        return routesH[endpoint.code].getBy(controllerClz);
    }


    /**
     * 移除路由关系
     *
     * @param pathPrefix 路径前缀
     */
    @Override
    public void remove(String pathPrefix) {
        routesH[Endpoint.before.code].remove(pathPrefix);
        routesH[Endpoint.main.code].remove(pathPrefix);
        routesH[Endpoint.after.code].remove(pathPrefix);
    }

    @Override
    public void remove(Class<?> controllerClz) {
        routesH[Endpoint.before.code].remove(controllerClz);
        routesH[Endpoint.main.code].remove(controllerClz);
        routesH[Endpoint.after.code].remove(controllerClz);
    }

    /**
     * 清空路由关系
     */
    @Override
    public void clear() {
        routesH[0].clear();
        routesH[1].clear();
        routesH[2].clear();
    }

    //
    // HandlerSlots 接口实现
    //

    /**
     * @deprecated 2.9
     */
    @Deprecated
    @Override
    public void before(String expr, MethodType method, int index, Handler handler) {
        add(expr, Endpoint.before, method, index, handler);
    }

    /**
     * @deprecated 2.9
     */
    @Deprecated
    @Override
    public void after(String expr, MethodType method, int index, Handler handler) {
        add(expr, Endpoint.after, method, index, handler);
    }

    /**
     * @deprecated 2.9
     */
    @Deprecated
    @Override
    public void add(String expr, MethodType method, Handler handler) {
        add(expr, Endpoint.main, method, handler);
    }
}