package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.Parameter;

/**
 * 路由拦截器-限制器（根据路由规则限制）
 *
 * @author noear
 * @since 2.3
 */
public class RouterInterceptorLimiter implements RouterInterceptor {
    protected final PathRule rule;
    private final RouterInterceptor interceptor;

    public RouterInterceptorLimiter(RouterInterceptor interceptor, PathRule rule) {
        this.rule = rule;
        this.interceptor = interceptor;
    }

    /**
     * 是否匹配
     */
    protected boolean isMatched(Context ctx) {
        return rule == null || rule.isEmpty() || rule.test(ctx.pathNew());
    }

    /**
     * 获取拦截器
     */
    public RouterInterceptor getInterceptor() {
        return interceptor;
    }

    /**
     * 路径匹配模式
     */
    @Override
    public PathRule pathPatterns() {
        return rule;
    }

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        if (isMatched(ctx)) {
            //执行拦截
            interceptor.doIntercept(ctx, mainHandler, chain);
        } else {
            //原路传递
            chain.doIntercept(ctx, mainHandler);
        }
    }

    @Override
    public void postArguments(Context ctx, ParamWrap[] args, Object[] vals) throws Throwable {
        if (isMatched(ctx)) {
            //执行拦截
            interceptor.postArguments(ctx, args, vals);
        }
    }

    @Override
    public Object postResult(Context ctx, Object result) throws Throwable {
        if (isMatched(ctx)) {
            return interceptor.postResult(ctx, result);
        } else {
            return result;
        }
    }
}