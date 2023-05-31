package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.PathRule;

/**
 * 路由规则拦截器（可附加路径规则:路径包函和路径排除）
 *
 * <pre><code>
 * @Configuration
 * public class Config{
 *     @Bean
 *     public RouterInterceptor demo1(){
 *         //原始“路由拦截器”
 *         return new RouterInterceptorImpl();
 *     }
 *
 *     @Bean
 *     public RouterInterceptor demo2_1(){
 *         //转为“路由规则拦截器”（可附加路径规则）
 *         return new RouterInterceptorImpl().role().exclude("/login");
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 2.3
 */
public class RouterRuleInterceptor implements RouterInterceptor {
    private RouterInterceptor interceptor;
    private PathRule pathRule = new PathRule();

    public RouterRuleInterceptor(RouterInterceptor interceptor) {
        this.interceptor = interceptor;
    }


    /**
     * 包函
     */
    public RouterRuleInterceptor include(String... pathPatterns) {
        pathRule.include(pathPatterns);

        return this;
    }

    /**
     * 排除
     */
    public RouterRuleInterceptor exclude(String... pathPatterns) {
        pathRule.exclude(pathPatterns);

        return this;
    }

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        if (pathRule.isEmpty() || pathRule.test(ctx.path())) {
            //使用注册的路由拦截器
            interceptor.doIntercept(ctx, mainHandler, chain);
        } else {
            //原始转发
            chain.doIntercept(ctx, mainHandler);
        }
    }
}
