package org.noear.solon.core;

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

    public List<RankEntity<Filter>> getFilterNodes() {
        return Collections.unmodifiableList(filterNodes);
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
     * 拦截器节点
     */
    private final List<RankEntity<RouterInterceptor>> interceptorNodes = new ArrayList<>();

    public List<RankEntity<RouterInterceptor>> getInterceptorNodes() {
        return Collections.unmodifiableList(interceptorNodes);
    }

    /**
     * 添加拦截器
     */
    public synchronized void addInterceptor(RouterInterceptor interceptor, int index) {
        if (interceptor instanceof PathLimiter) {
            interceptor = new RouterInterceptorLimiter(interceptor, ((PathLimiter) interceptor).pathRule());
        }

        interceptorNodes.add(new RankEntity<>(interceptor, index));
        interceptorNodes.sort(Comparator.comparingInt(f -> f.index));
    }

    /**
     * 执行拦截
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

    public ActionExecuteHandler getExecuteHandlerDefault(){
        return executeHandlerDefault;
    }
}
