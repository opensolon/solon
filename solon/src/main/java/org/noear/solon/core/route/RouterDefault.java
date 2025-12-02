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

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.ChainManager;
import org.noear.solon.core.FactoryManager;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.PathMatcher;
import org.noear.solon.core.util.PathUtil;

import java.util.*;
import java.util.function.Predicate;

/**
 * 通用路由器默认实现
 *
 * @author noear
 * @since 1.3
 * @since 3.0
 */
public class RouterDefault implements Router, HandlerSlots {
    private final ChainManager chains;

    public RouterDefault(ChainManager chains) {
        this.chains = chains;
    }

    /**
     * 路由表
     *
     * @since 1.3
     * @since 3.0
     */
    private final RoutingTable<Handler> table = new RoutingTableDefault<>();

    @Override
    public void caseSensitive(boolean caseSensitive) {
        PathMatcher.setCaseSensitive(caseSensitive);
    }

    private List<Map.Entry<String, Predicate<Class<?>>>> pathPrefixTester = new ArrayList<>();

    @Override
    public void addPathPrefix(String pathPrefix, Predicate<Class<?>> tester) {
        pathPrefixTester.add(new AbstractMap.SimpleEntry<>(pathPrefix, tester));
    }

    private void doAdd(String path, MethodType method, int index, Handler handler) {
        table.add(path, method, index, handler.version(), handler);
    }

    private String doGetPathPrefix(Class<?> clz) {
        for (Map.Entry<String, Predicate<Class<?>>> entry : pathPrefixTester) {
            if (entry.getValue().test(clz)) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * 添加路由关系 for Handler
     *
     * @param path    路径
     * @param method  方法
     * @param index   顺序位
     * @param handler 处理接口
     */
    @Override
    public void add(String path, MethodType method, int index, Handler handler) {
        if (pathPrefixTester.size() > 0) {
            //添加路径前缀支持
            String pp0;
            if (handler instanceof Action) {
                pp0 = doGetPathPrefix(((Action) handler).controller().rawClz());
            } else {
                pp0 = doGetPathPrefix(handler.getClass());
            }

            if (pp0 != null) {
                path = PathUtil.mergePath(pp0, path);
            }
        }

        this.doAdd(path, method, index, handler);
    }


    @Override
    public void add(String pathPrefix, BeanWrap bw, boolean remoting) {
        if (bw != null) {
            if (pathPrefixTester.size() > 0) {
                //添加路径前缀支持
                String pp0 = doGetPathPrefix(bw.rawClz());
                if (pp0 != null) {
                    if (pathPrefix == null) {
                        pathPrefix = pp0;
                    } else {
                        pathPrefix = PathUtil.mergePath(pp0, pathPrefix);
                    }
                }
            }

            FactoryManager.getGlobal()
                    .createLoader(bw, remoting)
                    .withPathPrefix(pathPrefix)
                    .load(this::doAdd);
        }
    }

    @Override
    public Result<Handler> matchMainAndStatus(Context ctx) {
        //不能从缓存里取，不然 pathNew 会有问题
        String pathNew = ctx.pathNew();
        MethodType method = MethodTypeUtil.valueOf(ctx.method());

        return table.matchOneAndStatus(pathNew, ctx.getVersion(), method);
    }

    @Override
    public Handler matchMain(Context ctx) {
        //不能从缓存里取，不然 pathNew 会有问题
        String pathNew = ctx.pathNew();
        MethodType method = MethodTypeUtil.valueOf(ctx.method());

        return table.matchOne(pathNew, ctx.getVersion(), method);
    }


    /**
     * 获取某个处理点的所有路由记录（管理用）
     *
     * @return 处理点的所有路由记录
     */
    @Override
    public Collection<Routing<Handler>> findAll() {
        return table.getAll();
    }

    /**
     * 获取某个路径的某个处理点的路由记录（管理用）
     *
     * @param pathPrefix 路径前缀
     * @return 路径处理点的路由记录
     * @since 2.6
     */
    @Override
    public Collection<Routing<Handler>> findBy(String pathPrefix) {
        return table.getBy(pathPrefix);
    }

    @Override
    public Collection<Routing<Handler>> findBy(Class<?> controllerClz) {
        return table.getBy(controllerClz);
    }

    /**
     * 移除路由关系
     *
     * @param pathPrefix 路径前缀
     */
    @Override
    public void remove(String pathPrefix) {
        table.remove(pathPrefix);
    }

    @Override
    public void remove(Class<?> controllerClz) {
        table.remove(controllerClz);
    }

    /**
     * 清空路由关系
     */
    @Override
    public void clear() {
        table.clear();
    }

    @Override
    public void filter(int index, Filter filter) {
        chains.addFilter(filter, index);
    }

    @Override
    public void filterIfAbsent(int index, Filter filter) {
        chains.addFilterIfAbsent(filter, index);
    }

    @Override
    public void routerInterceptor(int index, RouterInterceptor interceptor) {
        chains.addRouterInterceptor(interceptor, index);
    }

    @Override
    public void routerInterceptorIfAbsent(int index, RouterInterceptor interceptor) {
        chains.addRouterInterceptorIfAbsent(interceptor, index);
    }

    //
    // HandlerSlots 接口实现
    //

    @Override
    public void add(String expr, MethodType method, Handler handler) {
        add(expr, method, 0, handler);
    }
}