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

/**
 * 算数操作符
 *
 * @author noear
 * @since 3.1
 */
public enum ArithmeticOp {
    add("+") {
        @Override
        public Object calculate(Class<?> inferType, Number a, Number b) {
            // 处理字符串拼接的特殊情况（在 ArithmeticNode 中提前处理）
            return numericCalculate(inferType, a, b,
                    Double::sum,
                    Float::sum,
                    Long::sum,
                    Integer::sum);
        }
    },
    sub("-") {
        @Override
        public Object calculate(Class<?> inferType, Number a, Number b) {
            return numericCalculate(inferType, a, b,
                    (x, y) -> x - y,
                    (x, y) -> x - y,
                    (x, y) -> x - y,
                    (x, y) -> x - y);
        }
    },
    mul("*") {
        @Override
        public Object calculate(Class<?> inferType, Number a, Number b) {
            return numericCalculate(inferType, a, b,
                    (x, y) -> x * y,
                    (x, y) -> x * y,
                    (x, y) -> x * y,
                    (x, y) -> x * y);
        }
    },
    div("/") {
        @Override
        public Object calculate(Class<?> inferType, Number a, Number b) {
            return numericCalculate(inferType, a, b,
                    (x, y) -> x / y,
                    (x, y) -> x / y,
                    (x, y) -> x / y,
                    (x, y) -> x / y);
        }
    },
    mod("%") {
        @Override
        public Object calculate(Class<?> inferType, Number a, Number b) {
            return numericCalculate(inferType, a, b,
                    (x, y) -> x % y,
                    (x, y) -> x % y,
                    (x, y) -> x % y,
                    (x, y) -> x % y);
        }
    };

    private final String code;
    private static final Class<?>[] NUMERIC_TYPES = {
            Double.class, Float.class, Long.class, Integer.class
    };

    ArithmeticOp(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * 执行数值计算（自动处理类型提升）
     */
    protected static Number numericCalculate(Class<?> inferType, Number a, Number b,
                                             DoubleBinaryOperator doubleOp,
                                             FloatBinaryOperator floatOp,
                                             LongBinaryOperator longOp,
                                             IntBinaryOperator intOp) {
        // 确定最高优先级类型
        if (inferType == null) {
            inferType = getPriorityType(a, b);
        }

        if (inferType == Double.class) {
            return doubleOp.applyAsDouble(a.doubleValue(), b.doubleValue());
        } else if (inferType == Float.class) {
            return floatOp.applyAsFloat(a.floatValue(), b.floatValue());
        } else if (inferType == Long.class) {
            return longOp.applyAsLong(a.longValue(), b.longValue());
        } else {
            return intOp.applyAsInt(a.intValue(), b.intValue());
        }
    }

    /**
     * 获取最高优先级的数值类型
     */
    public static Class<?> getPriorityType(Number a, Number b) {
        for (Class<?> type : NUMERIC_TYPES) {
            if (a.getClass() == type || b.getClass() == type) {
                return type;
            }
        }
        return Integer.class; // 默认
    }

    /**
     * 抽象计算方法（由枚举实例实现）
     *
     * @param inferType 推送类型
     */
    public abstract Object calculate(Class<?> inferType, Number a, Number b);

    @FunctionalInterface
    private interface DoubleBinaryOperator {
        double applyAsDouble(double a, double b);
    }

    @FunctionalInterface
    private interface FloatBinaryOperator {
        float applyAsFloat(float a, float b);
    }

    @FunctionalInterface
    private interface LongBinaryOperator {
        long applyAsLong(long a, long b);
    }

    @FunctionalInterface
    private interface IntBinaryOperator {
        int applyAsInt(int a, int b);
    }

    // 在 ArithmeticOp 中添加解析方法
    public static ArithmeticOp parse(String op) {
        switch (op) {
            case "+":
                return add;
            case "-":
                return sub;
            case "*":
                return mul;
            case "/":
                return div;
            case "%":
                return mod;
            default:
                throw new IllegalArgumentException("Invalid operator: " + op);
        }
    }
}