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
 * Snel 表达式求值引擎快捷方式
 *
 * @author noear
 * @since 3.1
 */
public interface Snel {
    /**
     * 编译（即解析）
     */
    static Expression compile(Reader reader) {
        return SnelEvaluator.getInstance().compile(reader);
    }

    /**
     * 编译（即解析）
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
}