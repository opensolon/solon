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
package org.noear.solon.expression.snel;

import org.noear.solon.expression.Expression;

import java.util.Map;

/**
 * 三元表达式节点
 *
 * @author noear
 * @since 3.1
 */
public class TernaryNode implements Expression {
    private Expression<Boolean> condition; // 条件表达式
    private Expression trueExpression; // 条件为真时的表达式
    private Expression falseExpression; // 条件为假时的表达式

    public TernaryNode(Expression<Boolean> condition, Expression trueExpression, Expression falseExpression) {
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    @Override
    public Object evaluate(Map context) {
        // 计算条件表达式
        Boolean conditionResult = condition.evaluate(context);
        // 根据条件结果返回对应的表达式值
        return conditionResult ? trueExpression.evaluate(context) : falseExpression.evaluate(context);
    }
}