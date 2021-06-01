package org.noear.solon.extend.auth;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.ext.BiConsumerEx;

/**
 * 认证适配器（需要用户对接）
 *
 * @author noear
 * @since 1.4
 */
public class AuthAdapter {
    private String loginUrl;
    private BiConsumerEx<Context, Result> authOnFailure = (ctx, rst) -> {
    };
    private PathMatchers authPathMatchers = (ctx, url) -> true;
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

    public BiConsumerEx<Context, Result> authOnFailure() {
        return authOnFailure;
    }

    /**
     * 验证出错处理
     * */
    public AuthAdapter authOnFailure(BiConsumerEx<Context, Result> handler) {
        authOnFailure = handler;
        return this;
    }

    public PathMatchers authPathMatchers() {
        return authPathMatchers;
    }

    /**
     * 认证path匹配
     */
    public AuthAdapter authPathMatchers(PathMatchers matchers) {
        authPathMatchers = matchers;
        return this;
    }

    /**
     * 认证path拦截器
     * */
    public AuthAdapter authPathInterceptor(AuthInterceptor interceptor) {
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
