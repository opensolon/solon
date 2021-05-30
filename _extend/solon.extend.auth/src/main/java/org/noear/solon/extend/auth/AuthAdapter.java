package org.noear.solon.extend.auth;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * 认证适配器（需要用户对接）
 *
 * @author noear
 * @since 1.4
 */
public class AuthAdapter {
    private String loginUrl;
    private BiConsumer<Context, Result> authOnFailure = (ctx, rst) -> {
    };
    private BiPredicate<Context, String> authPathMatchers = (ctx, url) -> true;
    private AuthProcessor authProcessor;

    public String loginUrl() {
        return loginUrl;
    }

    /**
     * 登录Url
     */
    public AuthAdapter loginUrl(String url) {
        loginUrl = url;
        return this;
    }

    public BiConsumer<Context, Result> authOnFailure() {
        return authOnFailure;
    }

    /**
     * 验证出错处理
     * */
    public AuthAdapter authOnFailure(BiConsumer<Context, Result> handler) {
        authOnFailure = handler;
        return this;
    }

    public BiPredicate<Context, String> authPathMatchers() {
        return authPathMatchers;
    }

    /**
     * 认证Url匹配
     */
    public AuthAdapter authPathMatchers(BiPredicate<Context, String> matchers) {
        authPathMatchers = matchers;
        return this;
    }

    /**
     * 认证拦截器
     * */
    public AuthAdapter authInterceptor(AuthInterceptor interceptor) {
        Solon.global().before(interceptor);
        return this;
    }


    public AuthProcessor authProcessor() {
        return authProcessor;
    }


    /**
     * 认证处理器
     */
    public AuthAdapter authProcessor(AuthProcessor processor) {
        authProcessor = processor;
        return this;
    }
}
