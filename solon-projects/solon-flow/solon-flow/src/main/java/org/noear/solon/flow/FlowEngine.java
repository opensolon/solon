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

import org.noear.solon.lang.Preview;

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
     * 加载
     *
     * @param chain 链
     */
    void load(Chain chain);

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