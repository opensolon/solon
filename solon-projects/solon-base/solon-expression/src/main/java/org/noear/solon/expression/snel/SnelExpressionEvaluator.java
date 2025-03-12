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
import org.noear.solon.expression.ExpressionEvaluator;

/**
 * 简单表达式评估器
 *
 * @author noear
 * @since 3.1
 */
import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单表达式语言解析器
 *
 * @author noear
 * @since 3.1
 * */
public class SnelExpressionEvaluator implements ExpressionEvaluator {
    private static final SnelExpressionEvaluator instance = new SnelExpressionEvaluator();

    public static SnelExpressionEvaluator getInstance() {
        return instance;
    }

    /// /////////////

    private Map<String, Expression> exprCached = new ConcurrentHashMap<>();

    @Override
    public Object eval(String expr, Map context, boolean cached) {
        if(cached) {
            Expression expression = exprCached.computeIfAbsent(expr, k -> compile(expr));
            return expression.evaluate(context);
        }else{
            return compile(expr).evaluate(context);
        }
    }

    /// ////////////

    @Override
    public Expression compile(Reader reader) {
        ParserState state = new ParserState(reader);
        Expression result = parseTernaryExpression(state); // 解析三元表达式
        if (state.getCurrentChar() != -1) {
            throw new RuntimeException("Unexpected character: " + (char) state.getCurrentChar());
        }
        return result;
    }

    // 解析三元表达式
    private Expression parseTernaryExpression(ParserState state) {
        Expression condition = parseLogicalOrExpression(state);
        if (eat(state, '?')) {
            Expression trueExpression = parseLogicalOrExpression(state);
            eat(state, ':');
            Expression falseExpression = parseLogicalOrExpression(state);
            return new TernaryNode(condition, trueExpression, falseExpression);
        } else {
            return condition; // 如果不是三元表达式，直接返回条件
        }
    }

    // 解析逻辑 OR 表达式
    private Expression parseLogicalOrExpression(ParserState state) {
        Expression result = parseLogicalAndExpression(state);
        while (true) {
            if (eat(state, "OR") || eat(state, "||")) {
                result = new LogicalNode(LogicalOp.or, result, parseLogicalAndExpression(state));
            } else {
                return result;
            }
        }
    }

    // 解析逻辑 AND 表达式
    private Expression parseLogicalAndExpression(ParserState state) {
        Expression result = parseLogicalNotExpression(state);
        while (true) {
            if (eat(state, "AND") || eat(state, "&&")) {
                result = new LogicalNode(LogicalOp.and, result, parseLogicalNotExpression(state));
            } else {
                return result;
            }
        }
    }

    // 解析逻辑 NOT 表达式
    private Expression parseLogicalNotExpression(ParserState state) {
        if (eat(state, "NOT")) {
            return new LogicalNode(LogicalOp.not, parseComparisonExpression(state), null);
        } else {
            return parseComparisonExpression(state);
        }
    }

    // 解析比较表达式
    private Expression parseComparisonExpression(ParserState state) {
        Expression left = parseAdditiveExpression(state);
        state.skipWhitespace();

        // 检查是否是逻辑比较操作符
        if (state.getCurrentChar() == '>' || state.getCurrentChar() == '<' || state.getCurrentChar() == '=' || state.getCurrentChar() == '!') {
            String operator = parseComparisonOperator(state);
            Expression right = parseAdditiveExpression(state);
            return new ComparisonNode(ComparisonOp.parse(operator), left, right);
        } else if (eat(state, "IN")) {
            List<Object> values = parseList(state);
            return new ComparisonNode(ComparisonOp.in, left, new ConstantNode(values));
        } else if (eat(state, "LIKE")) {
            Expression right = parseAdditiveExpression(state);
            return new ComparisonNode(ComparisonOp.lk, left, right);
        } else if (eat(state, "NOT")) {
            if (eat(state, "IN")) {
                List<Object> values = parseList(state);
                return new ComparisonNode(ComparisonOp.nin, left, new ConstantNode(values));
            } else if (eat(state, "LIKE")) {
                Expression right = parseAdditiveExpression(state);
                return new ComparisonNode(ComparisonOp.nlk, left, right);
            } else {
                throw new RuntimeException("Invalid expression after NOT");
            }
        } else {
            return left; // 如果不是比较表达式，直接返回算术表达式
        }
    }

    // 解析加减法表达式
    private Expression parseAdditiveExpression(ParserState state) {
        Expression result = parseMultiplicativeExpression(state);
        while (true) {
            if (eat(state, '+')) {
                result = new ArithmeticNode(ArithmeticOp.add, result, parseMultiplicativeExpression(state));
            } else if (eat(state, '-')) {
                result = new ArithmeticNode(ArithmeticOp.sub, result, parseMultiplicativeExpression(state));
            } else {
                return result;
            }
        }
    }

    // 解析乘除法表达式
    private Expression parseMultiplicativeExpression(ParserState state) {
        Expression result = parsePrimaryExpression(state);
        while (true) {
            if (eat(state, '*')) {
                result = new ArithmeticNode(ArithmeticOp.mul, result, parsePrimaryExpression(state));
            } else if (eat(state, '/')) {
                result = new ArithmeticNode(ArithmeticOp.div, result, parsePrimaryExpression(state));
            } else if (eat(state, '%')) {
                result = new ArithmeticNode(ArithmeticOp.mod, result, parsePrimaryExpression(state));
            } else {
                return result;
            }
        }
    }

    // 解析基本表达式（数字、变量、括号表达式）
    private Expression parsePrimaryExpression(ParserState state) {
        state.skipWhitespace();
        if (eat(state, '(')) {
            Expression result = parseLogicalOrExpression(state); // 递归解析括号内的表达式
            eat(state, ')');
            return result;
        } else if (Character.isDigit(state.getCurrentChar())) {
            return new ConstantNode(parseNumber(state));
        } else if (state.isString()) {
            return new ConstantNode(parseString(state));
        } else {
            String identifier = parseIdentifier(state);
            state.skipWhitespace();

            // 检查是否是三元表达式
            if (eat(state, '?')) {
                Expression trueExpression = parseLogicalOrExpression(state); // 解析 true 分支
                eat(state, ':'); // 跳过 :
                Expression falseExpression = parseLogicalOrExpression(state); // 解析 false 分支
                return new TernaryNode(new VariableNode(identifier), trueExpression, falseExpression);
            } else {
                return new VariableNode(identifier); // 普通变量
            }
        }
    }

    // 解析比较操作符
    private String parseComparisonOperator(ParserState state) {
        StringBuilder sb = new StringBuilder();
        sb.append((char) state.getCurrentChar());
        state.nextChar();
        if (state.getCurrentChar() == '=') {
            sb.append((char) state.getCurrentChar());
            state.nextChar();
        }
        return sb.toString();
    }

    // 解析列表（用于 IN 和 NOT IN）
    private List<Object> parseList(ParserState state) {
        List<Object> values = new ArrayList<>();
        eat(state, '[');
        while (state.getCurrentChar() != ']') {
            values.add(parseValue(state, false));
            state.skipWhitespace();
            if (state.getCurrentChar() == ',') {
                state.nextChar();
                state.skipWhitespace();
            }
        }
        eat(state, ']');
        return values;
    }

    // 解析值（数字、字符串、变量）
    private Object parseValue(ParserState state, boolean allowVariable) {
        state.skipWhitespace();
        if (state.isString()) {
            return parseString(state);
        } else if (Character.isDigit(state.getCurrentChar())) {
            return parseNumber(state);
        } else {
            String tmp = parseIdentifier(state);
            if ("true".equals(tmp) || "false".equals(tmp)) {
                return Boolean.parseBoolean(tmp);
            } else {
                if (allowVariable) {
                    return new VariableNode(tmp);
                } else {
                    throw new RuntimeException("Unexpected value: " + tmp);
                }
            }
        }
    }

    // 解析字符串
    private String parseString(ParserState state) {
        char quote = (char) state.getCurrentChar();
        state.nextChar();
        StringBuilder sb = new StringBuilder();
        while (state.getCurrentChar() != quote) {
            sb.append((char) state.getCurrentChar());
            state.nextChar();
        }
        state.nextChar(); // 跳过结束引号
        return sb.toString();
    }

    // 解析数字
    private Number parseNumber(ParserState state) {
        StringBuilder sb = new StringBuilder();
        boolean isFloat = false; // 是否是浮点数
        boolean isDouble = false;
        boolean isLong = false;  // 是否是长整型

        // 解析整数部分
        while (Character.isDigit(state.getCurrentChar()) || state.getCurrentChar() == '.') {
            if (state.getCurrentChar() == '.') {
                isDouble = true; // 包含小数点，说明是浮点数
            }
            sb.append((char) state.getCurrentChar());
            state.nextChar();
        }

        // 检查是否有后缀（L/l 表示 long，F/f 表示 float，D/d 表示 double）
        if (Character.toUpperCase(state.getCurrentChar()) == 'L') {
            isLong = true;
            state.nextChar(); // 跳过后缀
        } else if (Character.toUpperCase(state.getCurrentChar()) == 'F') {
            isFloat = true;
            state.nextChar(); // 跳过后缀
        } else if (Character.toUpperCase(state.getCurrentChar()) == 'D') {
            isDouble = true;
            state.nextChar(); // 跳过后缀
        }

        String numberStr = sb.toString();
        try {
            if (isDouble) {
                //如果是双精度
                return Double.parseDouble(numberStr);
            } else if (isFloat) {
                // 如果是浮点数
                return Float.parseFloat(numberStr);
            } else if (isLong) {
                // 如果是长整型
                return Long.parseLong(numberStr);
            } else {
                // 默认解析为 int
                return Integer.parseInt(numberStr);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format: " + numberStr, e);
        }
    }

    // 解析标识符
    private String parseIdentifier(ParserState state) {
        StringBuilder sb = new StringBuilder();
        while (state.isIdentifier()) {
            sb.append((char) state.getCurrentChar());
            state.nextChar();
        }
        return sb.toString();
    }

    // 检查并跳过指定字符串
    private boolean eat(ParserState state, String expected) {
        state.skipWhitespace();
        for (int i = 0; i < expected.length(); i++) {
            if (state.getCurrentChar() != expected.charAt(i)) {
                return false;
            }
            state.nextChar();
        }
        return true;
    }

    // 检查并跳过指定字符
    private boolean eat(ParserState state, char expected) {
        state.skipWhitespace();
        if (state.getCurrentChar() == expected) {
            state.nextChar();
            return true;
        }
        return false;
    }

    // 封装解析器状态
    private static class ParserState {
        private final Reader reader;
        private int ch; // 当前字符

        public ParserState(Reader reader) {
            this.reader = reader;
            this.ch = -1; // 初始状态
            nextChar(); // 初始化读取第一个字符
        }

        public int getCurrentChar() {
            return ch;
        }

        public void nextChar() {
            try {
                ch = reader.read();
            } catch (IOException e) {
                throw new RuntimeException("Error reading character", e);
            }
        }

        public void skipWhitespace() {
            while (Character.isWhitespace(ch)) {
                nextChar();
            }
        }

        public boolean isString() {
            return getCurrentChar() == '\'' || getCurrentChar() == '"';
        }

        public boolean isIdentifier() {
            return Character.isLetterOrDigit(getCurrentChar()) || getCurrentChar() == '_';
        }
    }
}