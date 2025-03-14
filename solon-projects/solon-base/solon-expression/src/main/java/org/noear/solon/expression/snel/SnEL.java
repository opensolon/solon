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

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * Solon 表达式语言求值引擎快捷方式（简称，SnEL）
 *
 * @author noear
 * @since 3.1
 */
public interface SnEL {
    /**
     * 编译（将文件编译为一个可评估的表达式结构树，可反向转换）
     */
    static Expression compile(Reader reader) {
        return SnelEvaluator.getInstance().compile(reader);
    }

    /**
     * 编译（将文件编译为一个可评估的表达式结构树，可反向转换）
     */
    static Expression compile(String expr) {
        return compile(new StringReader(expr));
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
        return SnelEvaluator.getInstance().eval(expr, context, cached);
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
     * 编译（将文件编译为一个可评估的表达式结构树，可反向转换）
     */
    static Expression<String> compileTemplate(Reader reader) {
        return TemplateEvaluator.getInstance().compile(reader);
    }

    /**
     * 编译（将文件编译为一个可评估的表达式结构树，可反向转换）
     */
    static Expression<String> compileTemplate(String expr) {
        return compileTemplate(new StringReader(expr));
    }

    /// /////////////////


    /**
     * 评估
     *
     * @param expr    表达式
     * @param context 上下文
     * @param cached  是否带编译缓存
     */
    static String evalTemplate(String expr, Function context, boolean cached) {
        return TemplateEvaluator.getInstance().eval(expr, context, cached);
    }

    /**
     * 评估
     *
     * @param expr    表达式
     * @param context 上下文
     * @param cached  是否带编译缓存
     */
    static String evalTemplate(String expr, Map context, boolean cached) {
        return evalTemplate(expr, context::get, cached);
    }


    /**
     * 评估（带编译缓存）
     *
     * @param expr    表达式
     * @param context 上下文
     */
    static String evalTemplate(String expr, Function context) {
        return evalTemplate(expr, context, true);
    }

    /**
     * 评估（带编译缓存）
     *
     * @param expr    表达式
     * @param context 上下文
     */
    static String evalTemplate(String expr, Map context) {
        return evalTemplate(expr, context, true);
    }

    /**
     * 评估（带编译缓存）
     *
     * @param expr 表达式
     */
    static String evalTemplate(String expr) {
        return evalTemplate(expr, Collections.EMPTY_MAP, true);
    }
}