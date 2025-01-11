/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.flow.core;

import org.noear.solon.Utils;

/**
 * 任务（表达式参考：'F,tag/fun1;R,tag/rule1'）
 *
 * @author noear
 * @since 3.0
 * */
public class Task {
    private final Node element;
    private final String expr;

    /**
     * 附件（按需定制使用）
     */
    public Object attachment;//如果做扩展解析，用作存储位；（不解析，定制性更强）


    /**
     * @param taskExpr 任务表达式
     */
    public Task(Node element, String taskExpr) {
        this.element = element;
        this.expr = taskExpr;
    }

    /**
     * 所属节点
     * */
    public Node node() {
        return element;
    }

    /**
     * 表达式（示例："F:tag/fun1;R:tag/rule1" 或 "fun1()" 或 "[{t:'F',c:'tag/fun1'}]"）
     */
    public String expr() {
        return expr;
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return Utils.isEmpty(expr);
    }

    @Override
    public String toString() {
        return expr;
    }
}