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

import java.util.Arrays;

/**
 * 条件表达式构建器
 *
 * @author noear
 * @since 3.1
 */
public class ConditionBuilder {
    public LogicalNode and(Expression<Boolean> left, Expression<Boolean> right) {
        return new LogicalNode(LogicalOp.and, left, right);
    }

    public LogicalNode or(Expression<Boolean> left, Expression<Boolean> right) {
        return new LogicalNode(LogicalOp.or, left, right);
    }

    public LogicalNode not(Expression<Boolean> left) {
        return new LogicalNode(LogicalOp.not, left, null);
    }

    /// /////////

    public ComparisonNode lt(String field, Number value) {
        return new ComparisonNode(ComparisonOp.lt, new VariableNode(field), new ConstantNode(value));
    }

    public ComparisonNode lte(String field, Number value) {
        return new ComparisonNode(ComparisonOp.lte, new VariableNode(field), new ConstantNode(value));
    }

    public ComparisonNode gt(String field, Number value) {
        return new ComparisonNode(ComparisonOp.gt, new VariableNode(field), new ConstantNode(value));
    }

    public ComparisonNode gte(String field, Number value) {
        return new ComparisonNode(ComparisonOp.gte, new VariableNode(field), new ConstantNode(value));
    }

    public ComparisonNode eq(String field, Object value) {
        return new ComparisonNode(ComparisonOp.eq, new VariableNode(field), new ConstantNode(value));
    }

    public ComparisonNode neq(String field, Object value) {
        return new ComparisonNode(ComparisonOp.neq, new VariableNode(field), new ConstantNode(value));
    }

    public ComparisonNode in(String field, Object... values) {
        return new ComparisonNode(ComparisonOp.in, new VariableNode(field), new ConstantNode(Arrays.asList(values)));
    }
}