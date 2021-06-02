package org.noear.solon.extend.auth;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.ext.BiConsumerEx;
import org.noear.solon.extend.auth.impl.AuthRuleImpl;

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
    private BiConsumerEx<Context, Result> authOnFailure = (ctx, rst) -> {
    };

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


    /**
     * 添加授权规则
     * */
    public synchronized AuthAdapter authRuleAdd(Consumer<AuthRule> custom) {
        if (authRuleHandler == null) {
            authRuleHandler = new AuthRuleHandler();
            Solon.global().before(authRuleHandler);
        }

        AuthRuleImpl authRule = new AuthRuleImpl();
        authRuleHandler.rules().add(authRule);
        custom.accept(authRule);

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

}
