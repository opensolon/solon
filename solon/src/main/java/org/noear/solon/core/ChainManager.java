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
package org.noear.solon.core;

import org.noear.solon.SolonApp;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChainImpl;
import org.noear.solon.core.route.RouterInterceptorLimiter;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.lang.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 请求链管理
 *
 * @author noear
 * @since 1.12
 */
public class ChainManager {
    /**
     * 类型集合（用于重复检测）
     */
    private final Set<Class<?>> typeSet = new HashSet<>();

    /**
     * 过滤器 节点
     */
    private final List<RankEntity<Filter>> filterNodes = new ArrayList<>();
    private ContextPathFilter contextPathFilter;

    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    public Collection<Filter> getFilterNodes() {
        List<Filter> tmp = new ArrayList<>();

        for (RankEntity<Filter> entity : filterNodes) {
            tmp.add(entity.target);
        }

        return tmp;
    }

    /**
     * 添加过滤器
     */
    public void addFilter(Filter filter, int index) {
        if (filter instanceof ContextPathFilter) {
            contextPathFilter = (ContextPathFilter) filter;
            return;
        }

        SYNC_LOCK.lock();

        try {
            typeSet.add(filter.getClass());

            filterNodes.add(new RankEntity(filter, index));
            filterNodes.sort(Comparator.comparingInt(f -> f.index));
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 添加过滤器，如果有相同类的则不加
     */
    public void addFilterIfAbsent(Filter filter, int index) {
        if (filter instanceof ContextPathFilter) {
            contextPathFilter = (ContextPathFilter) filter;
            return;
        }

        SYNC_LOCK.lock();

        try {
            if (typeSet.contains(filter.getClass())) {
                return;
            }

            //有同步锁，就不复用上面的代码了
            typeSet.add(filter.getClass());

            filterNodes.add(new RankEntity(filter, index));
            filterNodes.sort(Comparator.comparingInt(f -> f.index));
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 执行过滤
     */
    public void doFilter(Context x, Handler lastHandler) throws Throwable {
        if (contextPathFilter == null) {
            new FilterChainImpl(filterNodes, lastHandler).doFilter(x);
        } else {
            contextPathFilter.doFilter(x, new FilterChainImpl(filterNodes, lastHandler));
        }
    }

    //=======================

    /**
     * 路由拦截器节点
     */
    private final List<RankEntity<RouterInterceptor>> interceptorNodes = new ArrayList<>();

    /**
     * 获取所有路由拦截器
     */
    public Collection<RouterInterceptor> getInterceptorNodes() {
        List<RouterInterceptor> tmp = new ArrayList<>();

        for (RankEntity<RouterInterceptor> entity : interceptorNodes) {
            if (entity.target instanceof RouterInterceptorLimiter) {
                tmp.add(((RouterInterceptorLimiter) entity.target).getInterceptor());
            } else {
                tmp.add(entity.target);
            }

        }

        return tmp;
    }

    /**
     * 添加路由拦截器
     */
    public void addInterceptor(RouterInterceptor interceptor, int index) {
        SYNC_LOCK.lock();

        try {
            typeSet.add(interceptor.getClass());

            interceptor = new RouterInterceptorLimiter(interceptor, interceptor.pathPatterns());
            interceptorNodes.add(new RankEntity<>(interceptor, index));
            interceptorNodes.sort(Comparator.comparingInt(f -> f.index));
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 添加路由拦截器，如果有相同类的则不加
     */
    public void addInterceptorIfAbsent(RouterInterceptor interceptor, int index) {
        SYNC_LOCK.lock();

        try {
            if (typeSet.contains(interceptor.getClass())) {
                return;
            }

            //有同步锁，就不复用上面的代码了
            typeSet.add(interceptor.getClass());

            interceptor = new RouterInterceptorLimiter(interceptor, interceptor.pathPatterns());
            interceptorNodes.add(new RankEntity<>(interceptor, index));
            interceptorNodes.sort(Comparator.comparingInt(f -> f.index));
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 移除路由拦截器
     */
    public <T extends RouterInterceptor> void removeInterceptor(Class<T> clz) {
        SYNC_LOCK.lock();

        try {
            typeSet.add(clz);

            interceptorNodes.removeIf(i -> {
                if (i.target instanceof RouterInterceptorLimiter) {
                    return ((RouterInterceptorLimiter) i.target).getInterceptor().getClass() == clz;
                } else {
                    return i.target.getClass() == clz;
                }
            });
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 执行路由拦截
     */
    public void doIntercept(Context x, Handler mainHandler, Handler lastHandler) throws Throwable {
        //先执行的，包住后执行的
        new RouterInterceptorChainImpl(interceptorNodes, lastHandler).doIntercept(x, mainHandler);
    }

    /**
     * 提交参数（action / invoke 执行前调用）
     */
    public void postArguments(Context x, ParamWrap[] args, Object[] vals) throws Throwable {
        //后执行的，包住先执行的（与 doIntercept 的顺序反了一下）
        for (int i = interceptorNodes.size() - 1; i >= 0; i--) {
            RankEntity<RouterInterceptor> e = interceptorNodes.get(i);
            e.target.postArguments(x, args, vals);
        }
    }

    /**
     * 提交结果（action / render 执行前调用）
     */
    public Object postResult(Context x, @Nullable Object result) throws Throwable {
        //后执行的，包住先执行的（与 doIntercept 的顺序反了一下）
        for (int i = interceptorNodes.size() - 1; i >= 0; i--) {
            RankEntity<RouterInterceptor> e = interceptorNodes.get(i);
            result = e.target.postResult(x, result);
        }

        return result;
    }

    //===================

    private final List<RankEntity<ReturnValueHandler>> returnHandlers = new ArrayList<>();

    public void addReturnHandler(ReturnValueHandler e) {
        addReturnHandler(e, 0);
    }

    public void addReturnHandler(ReturnValueHandler e, int index) {
        returnHandlers.add(new RankEntity<>(e, index));
        Collections.sort(returnHandlers);
    }

    public ReturnValueHandler getReturnHandler(Context ctx, Class<?> returnType) {
        for (RankEntity<ReturnValueHandler> entity : returnHandlers) {
            if (entity.target.matched(ctx, returnType)) {
                return entity.target;
            }
        }

        return null;
    }

    //=========

    /**
     * 动作默认执行器
     */
    private ActionExecuteHandler executeHandlerDefault; //todo:new ActionExecuteHandlerDefault();
    /**
     * 动作执行库
     */
    private List<RankEntity<ActionExecuteHandler>> executeHandlers = new ArrayList<>();

    public void defExecuteHandler(ActionExecuteHandler e) {
        if (e != null) {
            executeHandlerDefault = e;
        }
    }

    /**
     * 添加Action执行器
     */
    public void addExecuteHandler(ActionExecuteHandler e) {
        addExecuteHandler(e, 0);
    }

    /**
     * 添加Action执行器
     *
     * @param index 顺序位
     */
    public void addExecuteHandler(ActionExecuteHandler e, int index) {
        if (e != null) {
            executeHandlers.add(new RankEntity<>(e, index));
            Collections.sort(executeHandlers);
        }
    }

    /**
     * 移除Action执行器
     */
    public void removeExecuteHandler(Class<?> clz) {
        executeHandlers.removeIf(e -> clz.isInstance(e.target));
    }

    public ActionExecuteHandler getExecuteHandler(Context c, int paramSize) {
        String ct = c.contentType();

        if (paramSize > 0) {
            //
            //仅有参数时，才执行执行其它执行器
            //
            for (RankEntity<ActionExecuteHandler> me : executeHandlers) {
                if (me.target.matched(c, ct)) {
                    return me.target;
                }
            }
        }

        return getExecuteHandlerDefault();
    }

    public ActionExecuteHandler getExecuteHandlerDefault() {
        if (executeHandlerDefault == null) {
            return FactoryManager.getGlobal().mvcFactory().getExecuteHandlerDefault();
        } else {
            return executeHandlerDefault;
        }
    }

    /// ////

    private List<RankEntity<ActionArgumentResolver>> argumentResolvers = new ArrayList<>();

    /**
     * 添加 Action 参数分析器
     */
    public void addArgumentResolver(ActionArgumentResolver e) {
        addArgumentResolver(e, 0);
    }

    /**
     * 添加 Action 参数分析器
     *
     * @param index 顺序位
     */
    public void addArgumentResolver(ActionArgumentResolver e, int index) {
        if (e != null) {
            argumentResolvers.add(new RankEntity<>(e, index));
            Collections.sort(argumentResolvers);
        }
    }

    /**
     * 移除 Action 参数分析器
     */
    public void removeArgumentResolver(Class<?> clz) {
        argumentResolvers.removeIf(e -> clz.isInstance(e.target));
    }

    /**
     * 获取参数分析器
     */
    public ActionArgumentResolver getArgumentResolver(Context ctx, ParamWrap pWrap) {
        for (RankEntity<ActionArgumentResolver> me : argumentResolvers) {
            if (me.target.matched(ctx, pWrap)) {
                return me.target;
            }
        }

        return null;
    }

    //
    // SessionState 对接 //与函数同名，_开头
    //
    private SessionStateFactory _sessionStateFactory = (ctx) -> new SessionStateEmpty();
    private boolean _sessionStateUpdated;

    public SessionStateFactory getSessionStateFactory() {
        return _sessionStateFactory;
    }

    /**
     * 设置Session状态管理器
     *
     * @since 1.12
     * @since 3.0
     */
    public void setSessionStateFactory(SessionStateFactory ssf) {
        if (ssf != null) {
            _sessionStateFactory = ssf;
            _sessionStateUpdated = true;
        }
    }

    /**
     * 刷新Session状态
     *
     * @since 3.0
     */
    public void refreshSessionState(Context c) throws IOException {
        if (_sessionStateUpdated) {
            //替代 bef("**", MethodType.HTTP, ...)
            c.sessionState().sessionRefresh();
        }
    }

    /**
     * 获取Session状态
     */
    public SessionState getSessionState(Context ctx) {
        return _sessionStateFactory.create(ctx);
    }
}