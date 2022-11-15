package org.noear.solon.auth;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Note;
import org.noear.solon.auth.impl.AuthRuleImpl;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 认证适配器（由用户对接）
 *
 * @author noear
 * @since 1.4
 */
public class AuthAdapter {
    private String loginUrl;
    private String authRulePathPrefix;
    private AuthRuleHandler authRuleHandler;
    private AuthProcessor authProcessor;
    private AuthFailureHandler authFailure = new AuthFailureHandlerDefault();

    //=================//=================//=================

    /**
     * 获取登录Url
     * */
    public String loginUrl() {
        return loginUrl;
    }

    /**
     * 设置登录Url
     */
    public AuthAdapter loginUrl(String url) {
        loginUrl = url;
        return this;
    }

    //=================//=================//=================


    /**
     * 添加一个授权规则
     *
     * @param builder 规则构建器
     * */
    @Note("添加一个授权规则")
    public synchronized AuthAdapter addRule(Consumer<AuthRule> builder) {
        AuthRuleImpl rule = new AuthRuleImpl();
        builder.accept(rule);

        addRuleDo(rule);

        return this;
    }

    /**
     * 添加一批授权规则（构建规则）
     *
     * @param rules 规则集合
     * */
    public AuthAdapter addRules(Collection<AuthRule> rules) {
        rules.forEach(r -> addRuleDo(r));
        return this;
    }


    /**
     * 添加授权规则
     *
     * @param rule 规则
     * */
    private synchronized void addRuleDo(AuthRule rule) {
        if (authRuleHandler == null) {
            authRuleHandler = new AuthRuleHandler();
            authRuleHandler.setPathPrefix(authRulePathPrefix);

            Solon.app().before(authRuleHandler);
        }

        authRuleHandler.addRule(rule);
    }

    /**
     * 设置规则路径前缀（用于支持 AuthAdapterSupplier 的前缀特性）
     *
     * @param pathPrefix 路径前缀
     * */
    protected void setRulePathPrefix(String pathPrefix) {
        authRulePathPrefix = pathPrefix;

        if (authRuleHandler != null) {
            authRuleHandler.setPathPrefix(authRulePathPrefix);
        }
    }

    //=================//=================//=================

    /**
     * 获取认证处理器
     * */
    public AuthProcessor processor() {
        return authProcessor;
    }


    /**
     * 设定认证处理器
     *
     * @param processor 认证处理器
     */
    @Note("设定认证处理器")
    public AuthAdapter processor(AuthProcessor processor) {
        authProcessor = processor;
        return this;
    }

    //=================//=================//=================

    /**
     * 获取默认的验证出错处理
     * */
    public AuthFailureHandler failure() {
        return authFailure;
    }

    /**
     * 设定默认的验证出错处理
     * */
    @Note("设定默认的验证出错处理")
    public AuthAdapter failure(AuthFailureHandler handler) {
        authFailure = handler;
        return this;
    }
}
