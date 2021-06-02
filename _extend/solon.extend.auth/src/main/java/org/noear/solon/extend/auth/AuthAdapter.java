package org.noear.solon.extend.auth;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.ext.BiConsumerEx;
import org.noear.solon.extend.auth.impl.AuthRuleImpl;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 认证适配器（需要用户对接）
 *
 * @author noear
 * @since 1.4
 */
public class AuthAdapter {
    private String loginUrl;
    private AuthRuleHandler authRuleHandler;
    private AuthProcessor authProcessor;
    private BiConsumerEx<Context, Result> authFailure = (ctx, rst) -> ctx.render(rst);

    //=================//=================//=================

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

    //=================//=================//=================


    /**
     * 添加授权规则
     * */
    public synchronized AuthAdapter addRule(Consumer<AuthRule> custom) {
        AuthRuleImpl rule = new AuthRuleImpl();
        custom.accept(rule);

        return addRule(rule);
    }

    /**
     * 添加授权规则
     * */
    public synchronized AuthAdapter addRule(AuthRule rule) {
        if (authRuleHandler == null) {
            authRuleHandler = new AuthRuleHandler();
            Solon.global().before(authRuleHandler);
        }

        authRuleHandler.rules().add(rule);

        return this;
    }

    /**
     * 添加授权规则
     * */
    public AuthAdapter addRule(Collection<AuthRule> rules) {
        rules.forEach(r -> addRule(r));
        return this;
    }

    //=================//=================//=================

    /**
     * 添加授权规则
     * */
    public AuthProcessor processor() {
        return authProcessor;
    }


    /**
     * 认证处理器
     */
    public AuthAdapter processor(AuthProcessor processor) {
        authProcessor = processor;
        return this;
    }

    //=================//=================//=================

    public BiConsumerEx<Context, Result> failure() {
        return authFailure;
    }

    /**
     * 验证出错处理
     * */
    public AuthAdapter failure(BiConsumerEx<Context, Result> handler) {
        authFailure = handler;
        return this;
    }

}
