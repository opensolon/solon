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
 * 算数表达式节点
 *
 * @author noear
 * @since 3.1
 */
public class ArithmeticNode implements Expression {
    private ArithmeticOp operator; // 算数运算符，如 "+", "-", "*", "/"
    private Expression left;
    private Expression right;

    public ArithmeticNode(ArithmeticOp operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public ArithmeticOp getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public Object evaluate(Map context) {
        Object leftValue = left.evaluate(context);
        Object rightValue = right.evaluate(context);

        switch (operator) {
            case add: {
                if (leftValue instanceof Number && rightValue instanceof Number) {
                    if (leftValue instanceof Double || rightValue instanceof Double) {
                        return ((Number) leftValue).doubleValue() + ((Number) rightValue).doubleValue();
                    } else if (leftValue instanceof Float || rightValue instanceof Float) {
                        return ((Number) leftValue).floatValue() + ((Number) rightValue).floatValue();
                    } else if (leftValue instanceof Long || rightValue instanceof Long) {
                        return ((Number) leftValue).longValue() + ((Number) rightValue).longValue();
                    } else if (leftValue instanceof Integer || rightValue instanceof Integer) {
                        return ((Number) leftValue).intValue() + ((Number) rightValue).intValue();
                    } else if (leftValue instanceof Short || rightValue instanceof Short) {
                        return ((Number) leftValue).shortValue() + ((Number) rightValue).shortValue();
                    } else {
                        throw new RuntimeException("Invalid number: " + leftValue + ", " + rightValue);
                    }
                } else {
                    return String.valueOf(leftValue) + rightValue;
                }
            }
            case sub: {
                if (leftValue instanceof Number && rightValue instanceof Number) {
                    if (leftValue instanceof Double || rightValue instanceof Double) {
                        return ((Number) leftValue).doubleValue() - ((Number) rightValue).doubleValue();
                    } else if (leftValue instanceof Float || rightValue instanceof Float) {
                        return ((Number) leftValue).floatValue() - ((Number) rightValue).floatValue();
                    } else if (leftValue instanceof Long || rightValue instanceof Long) {
                        return ((Number) leftValue).longValue() - ((Number) rightValue).longValue();
                    } else if (leftValue instanceof Integer || rightValue instanceof Integer) {
                        return ((Number) leftValue).intValue() - ((Number) rightValue).intValue();
                    } else if (leftValue instanceof Short || rightValue instanceof Short) {
                        return ((Number) leftValue).shortValue() - ((Number) rightValue).shortValue();
                    } else {
                        throw new RuntimeException("Invalid number: " + leftValue + ", " + rightValue);
                    }
                } else {
                    throw new RuntimeException("Invalid number: " + leftValue + ", " + rightValue);
                }
            }
            case mul: {
                if (leftValue instanceof Number && rightValue instanceof Number) {
                    if (leftValue instanceof Double || rightValue instanceof Double) {
                        return ((Number) leftValue).doubleValue() * ((Number) rightValue).doubleValue();
                    } else if (leftValue instanceof Float || rightValue instanceof Float) {
                        return ((Number) leftValue).floatValue() * ((Number) rightValue).floatValue();
                    } else if (leftValue instanceof Long || rightValue instanceof Long) {
                        return ((Number) leftValue).longValue() * ((Number) rightValue).longValue();
                    } else if (leftValue instanceof Integer || rightValue instanceof Integer) {
                        return ((Number) leftValue).intValue() * ((Number) rightValue).intValue();
                    } else if (leftValue instanceof Short || rightValue instanceof Short) {
                        return ((Number) leftValue).shortValue() * ((Number) rightValue).shortValue();
                    } else {
                        throw new RuntimeException("Invalid number: " + leftValue + ", " + rightValue);
                    }
                } else {
                    throw new RuntimeException("Invalid number: " + leftValue + ", " + rightValue);
                }
            }
            case div: {
                if (leftValue instanceof Number && rightValue instanceof Number) {
                    if (leftValue instanceof Double || rightValue instanceof Double) {
                        return ((Number) leftValue).doubleValue() / ((Number) rightValue).doubleValue();
                    } else if (leftValue instanceof Float || rightValue instanceof Float) {
                        return ((Number) leftValue).floatValue() / ((Number) rightValue).floatValue();
                    } else if (leftValue instanceof Long || rightValue instanceof Long) {
                        return ((Number) leftValue).longValue() / ((Number) rightValue).longValue();
                    } else if (leftValue instanceof Integer || rightValue instanceof Integer) {
                        return ((Number) leftValue).intValue() / ((Number) rightValue).intValue();
                    } else if (leftValue instanceof Short || rightValue instanceof Short) {
                        return ((Number) leftValue).shortValue() / ((Number) rightValue).shortValue();
                    } else {
                        throw new RuntimeException("Invalid number: " + leftValue + ", " + rightValue);
                    }
                } else {
                    throw new RuntimeException("Invalid number: " + leftValue + ", " + rightValue);
                }
            }
            case mod: {
                if (leftValue instanceof Number && rightValue instanceof Number) {
                    if (leftValue instanceof Double || rightValue instanceof Double) {
                        return ((Number) leftValue).doubleValue() % ((Number) rightValue).doubleValue();
                    } else if (leftValue instanceof Float || rightValue instanceof Float) {
                        return ((Number) leftValue).floatValue() % ((Number) rightValue).floatValue();
                    } else if (leftValue instanceof Long || rightValue instanceof Long) {
                        return ((Number) leftValue).longValue() % ((Number) rightValue).longValue();
                    } else if (leftValue instanceof Integer || rightValue instanceof Integer) {
                        return ((Number) leftValue).intValue() % ((Number) rightValue).intValue();
                    } else if (leftValue instanceof Short || rightValue instanceof Short) {
                        return ((Number) leftValue).shortValue() % ((Number) rightValue).shortValue();
                    } else {
                        throw new RuntimeException("Invalid number: " + leftValue + ", " + rightValue);
                    }
                } else {
                    throw new RuntimeException("Invalid number: " + leftValue + ", " + rightValue);
                }
            }
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}