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
import org.noear.solon.expression.exception.EvaluationException;

import java.util.function.Function;


/**
 * 算数表达式节点
 *
 * @author noear
 * @since 3.1
 */
public class ArithmeticNode implements Expression {
    private final ArithmeticOp operator;
    private final Expression left;
    private final Expression right;
    private final Class<?> inferType;

    public ArithmeticNode(ArithmeticOp operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
        this.inferType = getInferType(left, right);
    }

    private Class<?> getInferType(Expression left, Expression right) {
        if (left instanceof ConstantNode && right instanceof ConstantNode) {
            Object lv = ((ConstantNode) left).getValue();
            Object rv = ((ConstantNode) right).getValue();
            if (lv instanceof Number && rv instanceof Number) {
                ArithmeticOp.getPriorityType((Number) lv, (Number) rv);
            }
        }

        return null;
    }

    @Override
    public Object eval(Function context) {
        Object leftVal = left.eval(context);
        Object rightVal = right.eval(context);

        // 特殊处理加法中的字符串拼接
        if (operator == ArithmeticOp.add) {
            if (leftVal instanceof String || rightVal instanceof String) {
                return String.valueOf(leftVal) + String.valueOf(rightVal);
            }
        }

        if (leftVal == null) {
            throw new EvaluationException("Arithmetic left value is null");
        }

        if (rightVal == null) {
            throw new EvaluationException("Arithmetic right value is null");
        }

        // 确保操作数类型正确
        if (inferType == null && !(leftVal instanceof Number && rightVal instanceof Number)) {
            throw new EvaluationException("Non-numeric operands for arithmetic operation: "
                    + leftVal.getClass() + " and " + rightVal.getClass());
        }

        try {
            return operator.calculate(inferType, (Number) leftVal, (Number) rightVal);
        } catch (ArithmeticException e) {
            throw new EvaluationException("Arithmetic error: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator.getCode() + " " + right + ")";
    }
}