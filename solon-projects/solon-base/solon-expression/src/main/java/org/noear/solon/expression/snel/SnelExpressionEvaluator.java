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

import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.noear.solon.expression.Expression;
import org.noear.solon.expression.ExpressionEvaluator;
import org.noear.solon.lang.Preview;


/**
 * Snel 表达式求值引擎。
 *
 * 支持以下特性：
 * 1. 变量访问：`user.name`、`order['created']['name']` 等嵌套属性
 * 2. 方法调用：`Math.add(1, 2)`、`user.getName()` 等
 * 3. 逻辑运算：AND/OR/NOT，支持短路逻辑
 * 4. 比较运算：>、<、==、!=、IN、LIKE 等
 * 5. 算术运算：+、-、*、/、%
 * 6. 三元表达式：condition ? trueExpr : falseExpr
 * 7. 布尔常量：直接解析 true/false
 * 8. 空值安全：属性或方法不存在时返回 null，避免 NPE
 *
 * @author noear
 * @since 3.1
 * */
@Preview("3.1")
public class SnelExpressionEvaluator implements ExpressionEvaluator {
    private static final SnelExpressionEvaluator instance = new SnelExpressionEvaluator();
    private final Map<String, Expression> exprCached = new ConcurrentHashMap<>();

    public static SnelExpressionEvaluator getInstance() {
        return instance;
    }

    @Override
    public Object eval(String expr, Function context, boolean cached) {
        // 使用缓存加速重复表达式求值
        if (cached) {
            return exprCached.computeIfAbsent(expr, k -> compile(k)).evaluate(context);
        } else {
            return compile(expr).evaluate(context);
        }
    }

    @Override
    public Expression compile(Reader reader) {
        ParserState state = new ParserState(reader);
        Expression result = parseTernaryExpression(state);
        if (state.getCurrentChar() != -1) {
            throw new RuntimeException("Unexpected trailing character: " + (char) state.getCurrentChar());
        }
        return result;
    }

    // 以下为递归下降解析器的核心方法 ----------------------------

    /**
     * 解析三元表达式：condition ? trueExpr : falseExpr
     */
    private Expression parseTernaryExpression(ParserState state) {
        Expression condition = parseLogicalOrExpression(state);
        if (eat(state, '?')) {
            Expression<Object> trueExpr = parseTernaryExpression(state);
            require(state, ':', "Expected ':' in ternary expression");
            Expression<Object> falseExpr = parseTernaryExpression(state);
            return new TernaryNode((Expression<Boolean>) condition, trueExpr, falseExpr);
        }
        return condition;
    }

    /**
     * 解析逻辑 OR（|| 或 OR）
     */
    private Expression parseLogicalOrExpression(ParserState state) {
        Expression left = parseLogicalAndExpression(state);
        state.skipWhitespace();
        while (eat(state, "OR") || eat(state, "||")) {
            left = new LogicalNode(LogicalOp.or, left, parseLogicalAndExpression(state));
        }
        return left;
    }

    /**
     * 解析逻辑 AND（&& 或 AND）
     */
    private Expression parseLogicalAndExpression(ParserState state) {
        Expression left = parseLogicalNotExpression(state);
        state.skipWhitespace();
        while (eat(state, "AND") || eat(state, "&&")) {
            left = new LogicalNode(LogicalOp.and, left, parseLogicalNotExpression(state));
        }
        return left;
    }

    /**
     * 解析逻辑 NOT（前置 NOT 运算符）
     */
    private Expression parseLogicalNotExpression(ParserState state) {
        if (eat(state, "NOT")) {
            return new LogicalNode(LogicalOp.not, parseComparisonExpression(state), null);
        }
        return parseComparisonExpression(state);
    }

    /**
     * 解析比较表达式（包括 IN/LIKE 等高级操作符）
     */
    private Expression parseComparisonExpression(ParserState state) {
        Expression left = parseAdditiveExpression(state);
        state.skipWhitespace();

        if (isComparisonOperatorStart(state.getCurrentChar())) {
            String op = parseComparisonOperator(state);
            return new ComparisonNode(ComparisonOp.parse(op), left, parseAdditiveExpression(state));
        } else if (eat(state, "IN")) {
            return new ComparisonNode(ComparisonOp.in, left, parseList(state));
        } else if (eat(state, "LIKE")) {
            return new ComparisonNode(ComparisonOp.lk, left, parseAdditiveExpression(state));
        } else if (eat(state, "NOT")) {
            if (eat(state, "IN")) {
                return new ComparisonNode(ComparisonOp.nin, left, parseList(state));
            } else if (eat(state, "LIKE")) {
                return new ComparisonNode(ComparisonOp.nlk, left, parseAdditiveExpression(state));
            }
            throw new RuntimeException("Invalid NOT expression");
        }
        return left;
    }

    /**
     * 解析加减法表达式
     */
    private Expression parseAdditiveExpression(ParserState state) {
        Expression left = parseMultiplicativeExpression(state);
        while (true) {
            if (eat(state, '+')) {
                left = new ArithmeticNode(ArithmeticOp.add, left, parseMultiplicativeExpression(state));
            } else if (eat(state, '-')) {
                left = new ArithmeticNode(ArithmeticOp.sub, left, parseMultiplicativeExpression(state));
            } else {
                break;
            }
        }
        return left;
    }

    /**
     * 解析乘除法表达式
     */
    private Expression parseMultiplicativeExpression(ParserState state) {
        Expression left = parsePrimaryExpression(state);
        while (true) {
            if (eat(state, '*')) {
                left = new ArithmeticNode(ArithmeticOp.mul, left, parsePrimaryExpression(state));
            } else if (eat(state, '/')) {
                left = new ArithmeticNode(ArithmeticOp.div, left, parsePrimaryExpression(state));
            } else if (eat(state, '%')) {
                left = new ArithmeticNode(ArithmeticOp.mod, left, parsePrimaryExpression(state));
            } else {
                break;
            }
        }
        return left;
    }

    /**
     * 解析基本表达式单元：
     * 1. 括号表达式 ( ... )
     * 2. 数字字面量（如 123, 45.67）
     * 3. 字符串字面量（如 'hello'）
     * 4. 布尔常量（true/false）
     * 5. 变量或属性访问（如 user.name）
     * 6. 方法调用（如 Math.add(1, 2)）
     */
    private Expression parsePrimaryExpression(ParserState state) {
        state.skipWhitespace();
        if (eat(state, '(')) {
            Expression expr = parseTernaryExpression(state);
            eat(state, ')');
            return expr;
        } else if (Character.isDigit(state.getCurrentChar())) {
            return new ConstantNode(parseNumber(state));
        } else if (state.isString()) {
            return new ConstantNode(parseString(state));
        } else if (checkKeyword(state, "true")) {
            return new ConstantNode(true);
        } else if (checkKeyword(state, "false")) {
            return new ConstantNode(false);
        } else if (checkKeyword(state, "null")) {
            return new ConstantNode(null);
        } else {
            return parseVariableOrMethodCall(state);
        }
    }

    /**
     * 解析变量、属性访问或方法调用
     */
    private Expression parseVariableOrMethodCall(ParserState state) {
        String identifier = parseIdentifier(state);
        Expression expr = new VariableNode(identifier);

        // 循环处理后续的属性访问操作（. 或 [] 或 ()）
        while (true) {
            state.skipWhitespace();
            if (eat(state, '.')) {
                // 解析点号属性访问：obj.property
                String prop = parseIdentifier(state);
                expr = new PropertyNode(expr, prop);
            } else if (eat(state, '[')) {
                // 解析方括号属性访问：obj['property'] 或 obj[0]
                Expression propExpr = parseLogicalOrExpression(state);
                eat(state, ']');
                expr = new PropertyNode(expr, propExpr);
            } else if (eat(state, '(')) {
                // 解析方法调用：obj.method()
                List<Expression> args = parseMethodArguments(state);
                eat(state, ')');

                // 确保 target 是属性访问节点，而不是方法名
                if (expr instanceof PropertyNode) {
                    PropertyNode propertyNode = (PropertyNode) expr;
                    expr = new MethodNode(propertyNode.getTarget(), propertyNode.getPropertyName(), args);
                } else if (expr instanceof VariableNode) {
                    // 如果 expr 是变量节点，直接使用方法名
                    expr = new MethodNode(expr, identifier, args);
                } else {
                    throw new RuntimeException("Invalid method call target: " + expr);
                }
            } else {
                break;
            }
        }
        return expr;
    }

    /**
     * 解析方法参数列表
     */
    private List<Expression> parseMethodArguments(ParserState state) {
        List<Expression> args = new ArrayList<>();
        while (state.getCurrentChar() != ')') {
            args.add(parseLogicalOrExpression(state));
            if (eat(state, ',')) continue;
        }
        return args;
    }

    // 以下为工具方法 --------------------------------

    /**
     * 检查字符是否是比较操作符的起始字符（>、<、=、!）
     */
    private boolean isComparisonOperatorStart(int c) {
        return c == '>' || c == '<' || c == '=' || c == '!';
    }

    /**
     * 解析比较操作符（支持 ==、!=、>=、<=）
     */
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

    /**
     * 解析列表（用于 IN 操作符）
     */
    private Expression parseList(ParserState state) {
        if (eat(state, '[')) {
            List<Object> list = new ArrayList<>();
            while (state.getCurrentChar() != ']') {
                list.add(parseValue(state));
                if (eat(state, ',')) continue;
            }
            eat(state, ']');
            return new ConstantNode(list);
        } else {
            return parseTernaryExpression(state);
        }
    }

    /**
     * 解析值（数字、字符串、变量）
     */
    private Object parseValue(ParserState state) {
        state.skipWhitespace();
        if (state.isString()) {
            return parseString(state);
        } else if (Character.isDigit(state.getCurrentChar())) {
            return parseNumber(state);
        } else if (checkKeyword(state, "true")) {
            return true;
        } else if (checkKeyword(state, "false")) {
            return false;
        } else if (checkKeyword(state, "null")) {
            return null;
        } else {
            return parseVariableOrMethodCall(state); // 简化处理
        }
    }

    /**
     * 解析字符串
     */
    private String parseString(ParserState state) {
        char quote = (char) state.getCurrentChar();
        state.nextChar();
        StringBuilder sb = new StringBuilder();
        while (state.getCurrentChar() != quote) {
            sb.append((char) state.getCurrentChar());
            state.nextChar();
        }
        state.nextChar();
        return sb.toString();
    }

    /**
     * 解析数字
     */
    private Number parseNumber(ParserState state) {
        StringBuilder sb = new StringBuilder();
        boolean isFloat = false;
        boolean isDouble = false;
        boolean isLong = false;

        while (Character.isDigit(state.getCurrentChar()) || state.getCurrentChar() == '.') {
            if (state.getCurrentChar() == '.') {
                isDouble = true;
            }
            sb.append((char) state.getCurrentChar());
            state.nextChar();
        }

        if (Character.toUpperCase(state.getCurrentChar()) == 'L') {
            isLong = true;
            state.nextChar();
        } else if (Character.toUpperCase(state.getCurrentChar()) == 'F') {
            isFloat = true;
            isDouble = false;
            state.nextChar();
        } else if (Character.toUpperCase(state.getCurrentChar()) == 'D') {
            isDouble = true;
            state.nextChar();
        }

        String numberStr = sb.toString();
        try {
            if (isDouble) {
                return Double.parseDouble(numberStr);
            } else if (isFloat) {
                return Float.parseFloat(numberStr);
            } else if (isLong) {
                return Long.parseLong(numberStr);
            } else {
                return Integer.parseInt(numberStr);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format: " + numberStr, e);
        }
    }

    /**
     * 解析标识符
     */
    private String parseIdentifier(ParserState state) {
        StringBuilder sb = new StringBuilder();
        while (state.isIdentifier()) {
            sb.append((char) state.getCurrentChar());
            state.nextChar();
        }
        return sb.toString();
    }

    /**
     * 检查并跳过指定字符串
     */
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

    /**
     * 检查并跳过指定字符
     */
    private boolean eat(ParserState state, char expected) {
        state.skipWhitespace();
        if (state.getCurrentChar() == expected) {
            state.nextChar();
            return true;
        }
        return false;
    }

    /**
     * 检查并跳过指定字符，否则抛出异常
     */
    private void require(ParserState state, char expected, String errorMessage) {
        if (!eat(state, expected)) {
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * 检查当前是否是关键字（如 true/false）
     */
    private boolean checkKeyword(ParserState state, String keyword) {
        state.mark();
        for (int i = 0; i < keyword.length(); i++) {
            if (state.getCurrentChar() != keyword.charAt(i)) {
                state.reset();
                return false;
            }
            state.nextChar();
        }
        if (state.isIdentifier()) {
            state.reset();
            return false;
        }
        return true;
    }

    // 内部类：封装解析状态 ----------------------------

    /**
     * 解析器状态跟踪器
     */
    private static class ParserState {
        private final BufferedReader reader;
        private int ch;      // 当前字符
        private int position = 0;
        private int markedCh = 0;
        private int markedPosition = 0;

        public ParserState(Reader reader) {
            if (reader instanceof BufferedReader) {
                this.reader = (BufferedReader) reader;
            } else {
                this.reader = new BufferedReader(reader);
            }
            nextChar(); // 初始化读取第一个字符
        }

        /**
         * 获取当前字符
         */
        public int getCurrentChar() {
            return ch;
        }

        /**
         * 前进到下一个字符
         */
        public void nextChar() {
            try {
                ch = reader.read();
                position++;
            } catch (IOException e) {
                throw new RuntimeException("Read error at position " + position, e);
            }
        }

        /**
         * 跳过空白字符
         */
        public void skipWhitespace() {
            while (Character.isWhitespace(ch)) nextChar();
        }

        /**
         * 检查当前是否是字符串起始字符（' 或 "）
         */
        public boolean isString() {
            return ch == '\'' || ch == '"';
        }

        /**
         * 检查当前是否是标识符字符（字母/数字/下划线）
         */
        public boolean isIdentifier() {
            return Character.isLetterOrDigit(ch) || ch == '_';
        }

        /**
         * 获取当前读取位置
         */
        public void mark() {
            try {
                reader.mark(position);
                markedCh = ch;
                markedPosition = position;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 设置读取位置（用于回滚）
         */
        public void reset() {
            try {
                reader.reset();
                ch = markedCh;
                position = markedPosition;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            return "ParserState{" +
                    "ch='" + (char) ch + "'" +
                    ", position=" + position +
                    '}';
        }
    }
}