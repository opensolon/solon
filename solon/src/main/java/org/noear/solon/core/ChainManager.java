package org.noear.solon.core;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.PathLimiter;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChainImpl;
import org.noear.solon.core.route.RouterInterceptorLimiter;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.lang.Nullable;

import java.util.*;

/**
 * 请求链管理
 *
 * @author noear
 * @since 1.12
 */
public class ChainManager {
    /**
     * 过滤器 节点
     */
    private final List<RankEntity<Filter>> filterNodes = new ArrayList<>();

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
    public synchronized void addFilter(Filter filter, int index) {
        filterNodes.add(new RankEntity(filter, index));
        filterNodes.sort(Comparator.comparingInt(f -> f.index));
    }

    /**
     * 执行过滤
     */
    public void doFilter(Context x) throws Throwable {
        new FilterChainImpl(filterNodes).doFilter(x);
    }

    //=======================

    /**
     * 路由拦截器节点
     */
    private final List<RankEntity<RouterInterceptor>> interceptorNodes = new ArrayList<>();

    /**
     * 获取所有路由拦截器
     * */
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
    public synchronized void addInterceptor(RouterInterceptor interceptor, int index) {
        if (interceptor instanceof PathLimiter) {
            interceptor = new RouterInterceptorLimiter(interceptor, ((PathLimiter) interceptor).pathRule());
        } else {
            interceptor = new RouterInterceptorLimiter(interceptor, interceptor.pathPatterns());
        }

        interceptorNodes.add(new RankEntity<>(interceptor, index));
        interceptorNodes.sort(Comparator.comparingInt(f -> f.index));
    }

    /**
     * 执行路由拦截
     */
    public void doIntercept(Context x, @Nullable Handler mainHandler) throws Throwable {
        new RouterInterceptorChainImpl(interceptorNodes).doIntercept(x, mainHandler);
    }

    /**
     * 提交结果（action / render 执行前调用）
     */
    public Object postResult(Context x, @Nullable Object result) throws Throwable {
        for (RankEntity<RouterInterceptor> e : interceptorNodes) {
            result = e.target.postResult(x, result);
        }

        return result;
    }

    //===================

    private final Map<Class<?>, ActionReturnHandler> returnHandlers = new LinkedHashMap<>();

    public void addReturnHandler(ActionReturnHandler e) {
        if (e != null) {
            returnHandlers.put(e.getClass(), e);
        }
    }

    public ActionReturnHandler getReturnHandler(Class<?> returnType) {
        for (ActionReturnHandler handler : returnHandlers.values()) {
            if (handler.matched(returnType)) {
                return handler;
            }
        }

        return null;
    }

    //=========

    /**
     * 动作默认执行器
     */
    private ActionExecuteHandler executeHandlerDefault = new ActionExecuteHandlerDefault();
    /**
     * 动作执行库
     */
    private Map<Class<?>, ActionExecuteHandler> executeHandlers = new LinkedHashMap<>();

    public void defExecuteHandler(ActionExecuteHandler e) {
        if (e != null) {
            executeHandlerDefault = e;
        }
    }

    /**
     * 添加Action执行器
     */
    public void addExecuteHandler(ActionExecuteHandler e) {
        if (e != null) {
            executeHandlers.put(e.getClass(), e);
        }
    }

    /**
     * 移除Action执行器
     */
    public void removeExecuteHandler(Class<?> clz) {
        executeHandlers.remove(clz);
    }

    public ActionExecuteHandler getExecuteHandler(Context c, int paramSize) {
        String ct = c.contentType();

        if (ct != null && paramSize > 0) {
            //
            //仅有参数时，才执行执行其它执行器
            //
            for (ActionExecuteHandler me : executeHandlers.values()) {
                if (me.matched(c, ct)) {
                    return me;
                }
            }
        }

        return executeHandlerDefault;
    }

    public ActionExecuteHandler getExecuteHandlerDefault() {
        return executeHandlerDefault;
    }


    //
    // SessionState 对接 //与函数同名，_开头
    //
    private static SessionStateFactory _sessionStateFactory = (ctx) -> new SessionStateEmpty();
    private static boolean _sessionStateUpdated;

    public static SessionStateFactory getSessionStateFactory() {
        return _sessionStateFactory;
    }

    /**
     * 设置Session状态管理器
     */
    public static void setSessionStateFactory(SessionStateFactory ssf) {
        if (ssf != null) {
            _sessionStateFactory = ssf;

            if (_sessionStateUpdated == false) {
                _sessionStateUpdated = true;

                Solon.app().before("**", MethodType.HTTP, (c) -> {
                    c.sessionState().sessionRefresh();
                });
            }
        }
    }

    /**
     * 获取Session状态管理器
     */
    public static SessionState getSessionState(Context ctx) {
        return _sessionStateFactory.create(ctx);
    }
}
