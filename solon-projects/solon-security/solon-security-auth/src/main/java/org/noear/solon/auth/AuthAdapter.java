/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.auth;

import org.noear.solon.Solon;
import org.noear.solon.auth.impl.AuthRuleImpl;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.core.handle.Handler;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 认证适配器（由用户对接）
 *
 * @author noear
 * @since 1.4
 * @since 3.1
 */
public class AuthAdapter implements Filter {
    private String loginUrl;
    private String authRulePathPrefix;
    private AuthRuleHandler authRuleHandler;
    private AuthProcessor authProcessor;
    private AuthFailureHandler authFailure = new AuthFailureHandlerDefault();
    private ReentrantLock SYNC_LOCK = new ReentrantLock();

    //=================//=================//=================

    /**
     * 获取登录Url
     */
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
     */
    public AuthAdapter addRule(Consumer<AuthRule> builder) {
        AuthRuleImpl rule = new AuthRuleImpl();
        builder.accept(rule);

        addRuleDo(rule);

        return this;
    }

    /**
     * 添加一批授权规则（构建规则）
     *
     * @param rules 规则集合
     */
    public AuthAdapter addRules(Collection<AuthRule> rules) {
        rules.forEach(r -> addRuleDo(r));
        return this;
    }


    /**
     * 添加授权规则
     *
     * @param rule 规则
     */
    private void addRuleDo(AuthRule rule) {
        SYNC_LOCK.lock();
        try {
            if (authRuleHandler == null) {
                authRuleHandler = new AuthRuleHandler();
                authRuleHandler.setPathPrefix(authRulePathPrefix);
            }

            authRuleHandler.addRule(rule);
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 设置规则路径前缀
     */
    public String pathPrefix() {
        return authRulePathPrefix;
    }

    /**
     * 设置规则路径前缀（用于支持 AuthAdapterSupplier 的前缀特性）
     *
     * @param pathPrefix 路径前缀
     */
    public AuthAdapter pathPrefix(String pathPrefix) {
        authRulePathPrefix = pathPrefix;

        if (authRuleHandler != null) {
            authRuleHandler.setPathPrefix(authRulePathPrefix);
        }

        return this;
    }

    //=================//=================//=================

    /**
     * 获取认证处理器
     */
    public AuthProcessor processor() {
        return authProcessor;
    }


    /**
     * 设定认证处理器
     *
     * @param processor 认证处理器
     */
    public AuthAdapter processor(AuthProcessor processor) {
        authProcessor = processor;
        return this;
    }

    //=================//=================//=================

    /**
     * 获取默认的验证出错处理
     */
    public AuthFailureHandler failure() {
        return authFailure;
    }

    /**
     * 设定默认的验证出错处理
     */
    public AuthAdapter failure(AuthFailureHandler handler) {
        authFailure = handler;
        return this;
    }

    /**
     * @since 3.1
     */
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (authRuleHandler != null) {
            Handler mainHandler = Solon.app().router().matchMain(ctx);
            authRuleHandler.handle(ctx, mainHandler);
        }

        chain.doFilter(ctx);
    }
}