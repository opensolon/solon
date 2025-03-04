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
package org.noear.solon.flow.intercept;

import org.noear.solon.core.util.ConsumerEx;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.ChainDriver;
import org.noear.solon.flow.Node;

import java.util.List;

/**
 * 链调用者
 *
 * @author noear
 * @since 3.1
 */
public class ChainInvocation {
    private final ChainDriver driver;
    private final ChainContext context;
    private final Node startNode;
    private final int evalDepth;

    private final List<RankEntity<ChainInterceptor>> interceptorList;
    private final ConsumerEx<ChainInvocation> handler;
    private int index;

    public ChainInvocation(ChainDriver driver, ChainContext context, Node startNode, int evalDepth, List<RankEntity<ChainInterceptor>> interceptorList, ConsumerEx<ChainInvocation> handler) {
        this.driver = driver;
        this.context = context;
        this.startNode = startNode;
        this.evalDepth = evalDepth;

        this.interceptorList = interceptorList;
        this.handler = handler;
        this.index = 0;
    }

    /**
     * 驱动器
     */
    public ChainDriver getDriver() {
        return driver;
    }

    /**
     * 上下文
     */
    public ChainContext getContext() {
        return context;
    }

    /**
     * 开始节点
     */
    public Node getStartNode() {
        return startNode;
    }

    /**
     * 评估深度
     */
    public int getEvalDepth() {
        return evalDepth;
    }

    /**
     * 调用
     */
    public void invoke() throws Throwable {
        if (index < interceptorList.size()) {
            interceptorList.get(index++).target.doIntercept(this);
        } else {
            handler.accept(this);
        }
    }
}
