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
package org.noear.solon.flow.core;

import org.noear.solon.Utils;


/**
 * 条件（一般用于分支条件）
 *
 * @author noear
 * @since 3.0
 * */
public class Condition {
    private final NodeLink link;
    private final String expr;

    /**
     * 附件（按需定制使用）
     */
    public Object attachment;//如果做扩展解析，用作存储位；（不解析，定制性更强）

    /**
     * @param conditionsExpr 条件表达式
     */
    public Condition(NodeLink link, String conditionsExpr) {
        this.link = link;
        this.expr = conditionsExpr;
    }

    /**
     * 所属线
     */
    public NodeLink link() {
        return link;
    }

    /**
     * 表达式（示例："(a,>,12) and (b,=,1)" 或 "a=12 && b=1" 或 "[{l:'a',p:'>',r:'12'}...]"）
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