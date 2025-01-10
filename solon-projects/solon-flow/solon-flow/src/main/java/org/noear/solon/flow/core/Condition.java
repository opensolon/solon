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

import java.util.List;

/**
 * 条件（一般用于分支条件）
 *
 * @author noear
 * @since 3.0
 * */
public class Condition {
    private String name;
    private String expr;
    private List<ConditionItem> items = null;

    /**
     * 名字
     */
    public String name() {
        return name;
    }

    /**
     * 表达式
     */
    public String expr() {
        return expr;
    }

    /**
     * 条件项
     */
    public List<ConditionItem> items() {
        return items;
    }

    /**
     * @param name           名字
     * @param conditionsExpr 条件表达式
     */
    public Condition(String name, String conditionsExpr) {
        this.name = name;
        this.expr = conditionsExpr;
        this.items = ConditionItem.parse(conditionsExpr);
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return items == null || items.size() == 0;
    }
}