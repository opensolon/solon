package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.PathRule;

/**
 * 带路径规则的路由拦截器（可附加路径规则:路径包函和路径排除）
 *
 * <pre><code>
 * @Configuration
 * public class Config{
 *     @Bean
 *     public RouterInterceptor demo1(){
 *         //原始“路由拦截器” //路由控制内部处理
 *         return new RouterInterceptorImpl();
 *     }
 *
 *     @Bean
 *     public RouterInterceptor demo2_1(){
 *         //带规则的“路由拦截器”（可附加路径规则）//也可内部配置规则
 *         return new RouterRuleInterceptorImpl().exclude("/login");
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 2.3
 */
public abstract class RouterRuleInterceptor implements RouterInterceptor {
    protected final PathRule pathRule = new PathRule();

    /**
     * 是否匹配
     */
    protected boolean isMatched(Context ctx) {
        return pathRule.isEmpty() || pathRule.test(ctx.path());
    }

    /**
     * 添加包函
     */
    public RouterRuleInterceptor include(String... pathPatterns) {
        pathRule.include(pathPatterns);
        return this;
    }

    /**
     * 添加排除
     */
    public RouterRuleInterceptor exclude(String... pathPatterns) {
        pathRule.exclude(pathPatterns);
        return this;
    }

    /**
     * 执行截拦
     * */
    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        if (isMatched(ctx)) {
            //检测通过，则执行规则截拦
            doRuleIntercept(ctx, mainHandler, chain);
        } else {
            //原始转发
            chain.doIntercept(ctx, mainHandler);
        }
    }

    /**
     * 执行规则截拦
     * */
    protected abstract void doRuleIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable;
}
