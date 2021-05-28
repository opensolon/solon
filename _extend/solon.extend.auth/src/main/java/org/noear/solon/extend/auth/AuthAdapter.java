package org.noear.solon.extend.auth;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.Result;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 认证适配器（需要用户对接）
 *
 * @author noear
 * @since 1.4
 */
public class AuthAdapter {
    private static AuthAdapter global = new AuthAdapter();

    public static AuthAdapter global() {
        return global;
    }

    public static void globalSet(AuthAdapter global) {
        if (global != null) {
            AuthAdapter.global = global;
        }
    }


    private String loginUrl;
    private String loginProcessingUrl;
    private String usernameParam;
    private String passwordParam;
    private String logoutUrl;
    private BiConsumer<Context, Result> authOnFailure = (ctx, rst) -> {
    };
    private BiPredicate<Context, String> authUrlMatchers = (ctx, url) -> true;
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


    public String loginProcessingUrl() {
        return loginProcessingUrl;
    }

    /**
     * 登录处理Url
     */
    public AuthAdapter loginProcessingUrl(String url) {
        loginProcessingUrl = url;
        return this;
    }

    public String usernameParam() {
        return usernameParam;
    }

    /**
     * 用户名参数名
     */
    public AuthAdapter usernameParam(String name) {
        usernameParam = name;
        return this;
    }

    public String passwordParam() {
        return passwordParam;
    }

    /**
     * 密码参数名
     */
    public AuthAdapter passwordParam(String name) {
        passwordParam = name;
        return this;
    }

    public String logoutUrl() {
        return logoutUrl;
    }

    /**
     * 退出Url
     */
    public AuthAdapter logoutUrl(String url) {
        logoutUrl = url;
        return this;
    }

    public BiConsumer<Context, Result> authOnFailure() {
        return authOnFailure;
    }

    public AuthAdapter authOnFailure(BiConsumer<Context, Result> handler) {
        authOnFailure = handler;
        return this;
    }

    public BiPredicate<Context, String> authUrlMatchers() {
        return authUrlMatchers;
    }

    /**
     * 认证Url匹配
     */
    public AuthAdapter authUrlMatchers(BiPredicate<Context, String> tester) {
        authUrlMatchers = tester;
        return this;
    }


    public AuthProcessor authProcessor() {
        return authProcessor;
    }


    /**
     * 认证处理器匹配
     */
    public AuthAdapter authProcessor(AuthProcessor processor) {
        authProcessor = processor;
        return this;
    }
}
