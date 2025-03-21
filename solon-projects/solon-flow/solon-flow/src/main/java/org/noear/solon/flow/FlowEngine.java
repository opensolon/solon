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
package org.noear.solon.flow;

import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.flow.intercept.ChainInterceptor;
import org.noear.solon.lang.Preview;

import java.io.IOException;
import java.util.Collection;

/**
 * 流引擎
 *
 * @author noear
 * @since 3.0
 * */
@Preview("3.0")
public interface FlowEngine {
    /**
     * 新实例
     */
    static FlowEngine newInstance() {
        return new FlowEngineImpl();
    }

    /**
     * 添加拦截器
     *
     * @param index       顺序位
     * @param interceptor 拦截器
     */
    void addInterceptor(ChainInterceptor interceptor, int index);


    /**
     * 注册链驱动器
     *
     * @param name   名字
     * @param driver 驱动器
     */
    void register(String name, ChainDriver driver);

    /**
     * 注册默认链驱动器
     *
     * @param driver 默认驱动器
     */
    default void register(ChainDriver driver) {
        register("", driver);
    }

    /**
     * 注销链驱动器
     */
    void unregister(String name);


    /**
     * 解析配置文件
     *
     * @param chainUri 链资源地址
     */
    default void load(String chainUri) throws IOException {
        if (chainUri.contains("*")) {
            for (String u1 : ResourceUtil.scanResources(chainUri)) {
                load(Chain.parseByUri(u1));
            }
        } else {
            load(Chain.parseByUri(chainUri));
        }
    }

    /**
     * 加载链
     *
     * @param chain 链
     */
    void load(Chain chain);

    /**
     * 卸载链
     *
     * @param chainId 链Id
     */
    void unload(String chainId);

    /**
     * 获取链集合
     */
    Collection<Chain> chains();

    /**
     * 评估
     *
     * @param chainId 链Id
     */
    default void eval(String chainId) throws Throwable {
        eval(chainId, new ChainContext());
    }

    /**
     * 评估
     *
     * @param chainId 链Id
     * @param context 上下文
     */
    default void eval(String chainId, ChainContext context) throws Throwable {
        eval(chainId, null, -1, context);
    }

    /**
     * 评估
     *
     * @param chainId 链Id
     * @param startId 开始Id
     * @param depth   执行深度
     * @param context 上下文
     */
    void eval(String chainId, String startId, int depth, ChainContext context) throws Throwable;

    /**
     * 评估
     *
     * @param chain 链
     */
    default void eval(Chain chain) throws Throwable {
        eval(chain, new ChainContext());
    }

    /**
     * 评估
     *
     * @param chain   链
     * @param context 上下文
     */
    default void eval(Chain chain, ChainContext context) throws Throwable {
        eval(chain, null, -1, context);
    }

    /**
     * 评估
     *
     * @param chain   链
     * @param startId 开始Id
     * @param depth   执行深度
     * @param context 上下文
     */
    void eval(Chain chain, String startId, int depth, ChainContext context) throws Throwable;
}