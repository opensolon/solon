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

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * Solon 表达式语言引擎快捷方式（简称，SnEL）
 *
 * @author noear
 * @since 3.1
 */
public interface SnEL {
    /**
     * 解析（将文本解析为一个可评估的表达式结构树，可反向转换）
     */
    static Expression parse(String expr, boolean cached) {
        return SnelEvaluateParser.getInstance().parse(expr, cached);
    }

    static Expression parse(String expr) {
        return parse(expr, true);
    }


    /// /////////////////


    /**
     * 评估
     *
     * @param expr    表达式
     * @param context 上下文
     * @param cached  是否带编译缓存
     */
    static Object eval(String expr, Function context, boolean cached) {
        return parse(expr, cached).eval(context);
    }

    /**
     * 评估
     *
     * @param expr    表达式
     * @param context 上下文
     * @param cached  是否带编译缓存
     */
    static Object eval(String expr, Map context, boolean cached) {
        return eval(expr, context::get, cached);
    }


    /**
     * 评估（带编译缓存）
     *
     * @param expr    表达式
     * @param context 上下文
     */
    static Object eval(String expr, Function context) {
        return eval(expr, context, true);
    }

    /**
     * 评估（带编译缓存）
     *
     * @param expr    表达式
     * @param context 上下文
     */
    static Object eval(String expr, Map context) {
        return eval(expr, context, true);
    }

    /**
     * 评估（带编译缓存）
     *
     * @param expr 表达式
     */
    static Object eval(String expr) {
        return eval(expr, Collections.EMPTY_MAP, true);
    }

    /// /////////////////////////////

    /**
     * 上下文中的属性键（用于支持属性表达式）
     */
    static final String CONTEXT_PROPS_KEY = "$PROPS";


    /**
     * 解析模板
     */
    static Expression<String> parseTmpl(String expr, boolean cached) {
        return SnelTemplateParser.getInstance().parse(expr, cached);
    }

    static Expression<String> parseTmpl(String expr) {
        return parseTmpl(expr, true);
    }

    /// /////////////////


    /**
     * 评估模板
     *
     * @param expr    表达式
     * @param context 上下文
     * @param cached  是否带编译缓存
     */
    static String evalTmpl(String expr, Function context, boolean cached) {
        return parseTmpl(expr, cached).eval(context);
    }

    /**
     * 评估模板
     *
     * @param expr    表达式
     * @param context 上下文
     * @param cached  是否带编译缓存
     */
    static String evalTmpl(String expr, Map context, boolean cached) {
        return evalTmpl(expr, context::get, cached);
    }


    /**
     * 评估模板（带编译缓存）
     *
     * @param expr    表达式
     * @param context 上下文
     */
    static String evalTmpl(String expr, Function context) {
        return evalTmpl(expr, context, true);
    }

    /**
     * 评估模板（带编译缓存）
     *
     * @param expr    表达式
     * @param context 上下文
     */
    static String evalTmpl(String expr, Map context) {
        return evalTmpl(expr, context, true);
    }

    /**
     * 评估模板（带编译缓存）
     *
     * @param expr 表达式
     */
    static String evalTmpl(String expr) {
        return evalTmpl(expr, Collections.EMPTY_MAP, true);
    }
}