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

import org.noear.solon.SolonApp;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.ChainManager;
import org.noear.solon.core.Constants;
import org.noear.solon.core.handle.*;

/**
 * 路由包装器（更简单的使用路由）
 *
 * @author noear
 * @since 1.8
 * @since 3.0
 */
public abstract class RouterWrapper {
    private Router _router;
    private RouterHandler _routerHandler;
    private ChainManager _chainManager;
    private SolonApp _app;

    public abstract AppContext context();

    protected void initRouter(SolonApp app) {
        //顺序不能换
        _app = app;
        _chainManager = new ChainManager(app);
        _router = new RouterDefault(_chainManager);
        _routerHandler = new RouterHandler(_router, _chainManager);
    }

    /**
     * 路由器处理器
     */
    public RouterHandler routerHandler() {
        return _routerHandler;
    }

    /**
     * 路由器
     */
    public Router router() {
        return _router;
    }

    /**
     * 处理链管理器
     *
     * @deprecated 3.7 {@link #chains()}
     */
    @Deprecated
    public ChainManager chainManager() { //预计 v4.0 后标为弃用
        return chains();
    }

    /**
     * 处理链管理器
     *
     * @since 3.6
     */
    public ChainManager chains() {
        return _chainManager;
    }


    ///////////////////////////////////////////////
    //
    // 以下为web handler 有关
    //
    //////////////////////////////////////////////

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param filter 过滤器
     * @deprecated 3.7 {@link Router#filter(Filter)}
     */
    @Deprecated
    public void filter(Filter filter) {
        filter(0, filter);
    }

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param index  顺序位
     * @param filter 过滤器
     * @since 1.5
     * @deprecated 3.7 {@link Router#filter(int, Filter)}
     */
    @Deprecated
    public void filter(int index, Filter filter) {
        _router.filter(index, filter);
    }

    /**
     * 添加过滤器（按先进后出策略执行）,如果有相同类的则不加
     *
     * @param index  顺序位
     * @param filter 过滤器
     * @since 2.6
     * @deprecated 3.7 {@link Router#filterIfAbsent(int, Filter)}
     */
    @Deprecated
    public void filterIfAbsent(int index, Filter filter) {
        _router.filterIfAbsent(index, filter);
    }

    /**
     * 添加路由拦截器（按先进后出策略执行）
     *
     * @param interceptor 路由拦截器
     * @deprecated 3.7 {@link Router#routerInterceptor(RouterInterceptor)}
     */
    @Deprecated
    public void routerInterceptor(RouterInterceptor interceptor) {
        _router.routerInterceptor(interceptor);
    }


    /**
     * 添加路由拦截器（按先进后出策略执行）
     *
     * @param index       顺序位
     * @param interceptor 路由拦截器
     * @deprecated 3.7 {@link Router#routerInterceptor(int, RouterInterceptor)}
     */
    @Deprecated
    public void routerInterceptor(int index, RouterInterceptor interceptor) {
        _router.routerInterceptor(index, interceptor);
    }

    /**
     * 添加路由拦截器（按先进后出策略执行）,如果有相同类的则不加
     *
     * @param index       顺序位
     * @param interceptor 路由拦截器
     * @deprecated 3.7 {@link Router#routerInterceptorIfAbsent(int, RouterInterceptor)}
     */
    @Deprecated
    public void routerInterceptorIfAbsent(int index, RouterInterceptor interceptor) {
        _router.routerInterceptorIfAbsent(index, interceptor);
    }

    /**
     * 添加渲染器
     *
     * <pre>{@code
     * Solon.app().renders().register(name, render);
     * }</pre>
     *
     * @deprecated 3.6 {@link SolonApp#renders()#register(String, Render)}
     */
    @Deprecated
    public void render(String name, Render render) {
        _app.renders().register(name, render);
    }

    /**
     * 获取渲染器
     *
     * <pre>{@code
     * Solon.app().renders().get(name);
     * }</pre>
     *
     * @deprecated 3.6 {@link SolonApp#renders()#get(String)}
     */
    @Deprecated
    public Render render(String name) {
        return _app.renders().get(name);
    }

    /**
     * 获取 Json 渲染器
     *
     * @deprecated 3.6 {@link SolonApp#renders()#jsonOf()}
     */
    @Deprecated
    public Render renderOfJson() {
        return _app.renders().get(Constants.AT_JSON);
    }


    /**
     * 添加处理
     *
     * @deprecated 3.7 {@link Router#add(String, Class)}
     */
    @Deprecated
    public void add(String pathPrefix, Class<?> clz) {
        BeanWrap bw = context().wrapAndPut(clz);
        _router.add(pathPrefix, bw);
    }

    /**
     * 添加处理
     *
     * @deprecated 3.7 {@link Router#add(String, Class, boolean)}
     */
    @Deprecated
    public void add(String pathPrefix, Class<?> clz, boolean remoting) {
        BeanWrap bw = context().wrapAndPut(clz);
        bw.remotingSet(remoting);
        _router.add(pathPrefix, bw);
    }

    /**
     * 添加处理
     *
     * @deprecated 3.7 {@link Router#add(String, MethodType, Handler)}
     */
    @Deprecated
    public void add(String path, MethodType method, Handler handler) {
        _router.add(path, method, handler);
    }

    /**
     * 添加处理
     *
     * @deprecated 3.7 {@link Router#add(String, MethodType, int, Handler)}
     */
    @Deprecated
    public void add(String path, MethodType method, int index, Handler handler) {
        _router.add(path, method, index, handler);
    }

    /**
     * 添加所有方法处理
     *
     * @deprecated 3.7 {@link Router#all(String, Handler)}
     */
    @Deprecated
    public void all(String path, Handler handler) {
        add(path, MethodType.ALL, handler);
    }

    /**
     * 添加HTTP所有方法的处理（GET,POST,PUT,PATCH,DELETE,HEAD）
     *
     * @deprecated 3.7 {@link Router#http(String, Handler)}
     */
    @Deprecated
    public void http(String path, Handler handler) {
        add(path, MethodType.HTTP, handler);
    }

    /**
     * 添加HEAD方法的处理
     *
     * @deprecated 3.7 {@link Router#head(String, Handler)}
     */
    @Deprecated
    public void head(String path, Handler handler) {
        add(path, MethodType.HEAD, handler);
    }

    /**
     * 添加GET方法的处理（REST.select 从服务端获取一或多项资源）
     *
     * @deprecated 3.7 {@link Router#get(String, Handler)}
     */
    @Deprecated
    public void get(String path, Handler handler) {
        add(path, MethodType.GET, handler);
    }

    /**
     * 添加POST方法的处理（REST.create 在服务端新建一项资源）
     *
     * @deprecated 3.7 {@link Router#post(String, Handler)}
     */
    @Deprecated
    public void post(String path, Handler handler) {
        add(path, MethodType.POST, handler);
    }

    /**
     * 添加PUT方法的处理（REST.update 客户端提供改变后的完整资源）
     *
     * @deprecated 3.7 {@link Router#put(String, Handler)}
     */
    @Deprecated
    public void put(String path, Handler handler) {
        add(path, MethodType.PUT, handler);
    }

    /**
     * 添加PATCH方法的处理（REST.update 客户端提供改变的属性）
     *
     * @deprecated 3.7 {@link Router#patch(String, Handler)}
     */
    @Deprecated
    public void patch(String path, Handler handler) {
        add(path, MethodType.PATCH, handler);
    }

    /**
     * 添加DELETE方法的处理（REST.delete 从服务端删除资源）
     *
     * @deprecated 3.7 {@link Router#delete(String, Handler)}
     */
    @Deprecated
    public void delete(String path, Handler handler) {
        add(path, MethodType.DELETE, handler);
    }


    /**
     * 添加socket方法的监听
     *
     * @deprecated 3.7 {@link Router#socketd(String, Handler)}
     */
    @Deprecated
    public void socketd(String path, Handler handler) {
        add(path, MethodType.SOCKET, handler);
    }
}