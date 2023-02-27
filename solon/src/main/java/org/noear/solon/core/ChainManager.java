package org.noear.solon.core;

import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChainImpl;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.lang.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    private final List<RankEntity<Filter>> _filterNodes = new ArrayList<>();

    /**
     * 添加过滤器
     */
    public synchronized void addFilter(Filter filter, int order) {
        _filterNodes.add(new RankEntity(filter, order));
        _filterNodes.sort(Comparator.comparingInt(f -> f.order));
    }

    /**
     * 执行过滤
     */
    public void doFilter(Context x) throws Throwable {
        new FilterChainImpl(_filterNodes).doFilter(x);
    }


    /**
     * 拦截器节点
     */
    private final List<RankEntity<RouterInterceptor>> _interceptorNodes = new ArrayList<>();

    /**
     * 添加拦截器
     */
    public synchronized void addInterceptor(RouterInterceptor interceptor, int index) {
        _interceptorNodes.add(new RankEntity<>(interceptor, index));
        _interceptorNodes.sort(Comparator.comparingInt(f -> f.order));
    }

    /**
     * 执行拦截
     */
    public void doIntercept(Context x, @Nullable Handler mainHandler) throws Throwable {
        new RouterInterceptorChainImpl(_interceptorNodes).doIntercept(x, mainHandler);
    }

    /**
     * 提交结果（action / render 执行前调用）
     */
    public Object postResult(Context x, @Nullable Object result) throws Throwable {
        for (RankEntity<RouterInterceptor> e : _interceptorNodes) {
            result = e.target.postResult(x, result);
        }

        return result;
    }
}
