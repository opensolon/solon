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
package org.noear.solon.core.util;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.expression.exception.EvaluationException;
import org.noear.solon.expression.snel.SnEL;

import java.util.Map;
import java.util.function.Function;

/**
 * SnelUtil 表达式工具（为了保持与 TmplUtil 的兼容）
 *
 * @author noear
 * @since 3.6
 */
public class SnelUtil {
    /**
     * 执行模板表达式
     *
     * <pre>{@code
     * new: #{name}_#{order.id}_#{result.type}
     * old: ${name}_${order.id}_${.type}
     * }</pre>
     *
     * @param tmpl 模板
     * @param inv  调用
     */
    public static String evalTmpl(String tmpl, Invocation inv) {
        if (tmpl.indexOf('#') >= 0) {
            return SnEL.evalTmpl(tmpl, new InvocationContext(inv));
        } else if (tmpl.indexOf('$') >= 0) {
            //兼容旧模式
            return TmplUtil.parse(tmpl, inv);
        } else {
            return tmpl;
        }
    }

    /**
     * 执行模板表达式
     *
     * <pre>{@code
     * new: #{name}_#{order.id}
     * old: ${name}_${order.id}
     * }</pre>
     *
     * @param tmpl  模板（例：#{name}_#{order.id}）
     * @param model 数据模型
     */
    public static String evalTmpl(String tmpl, Map model) {
        if (tmpl.indexOf('#') >= 0) {
            return SnEL.evalTmpl(tmpl, model);
        } else if (tmpl.indexOf('$') >= 0) {
            //兼容旧模式
            return TmplUtil.parse(tmpl, model);
        } else {
            return tmpl;
        }
    }

    /**
     * 执行求值表达式
     */
    public static Object eval(String expr, Map model) {
        return SnEL.eval(expr, model);
    }

    static class InvocationContext implements Function<String, Object> {
        private final Invocation inv;

        public InvocationContext(Invocation inv) {
            this.inv = inv;
        }

        @Override
        public Object apply(String key) {
            if (inv.result() != null) {
                if ("result".equals(key)) {
                    return inv.result();
                }
            }

            Object rst = inv.argsAsMap().get(key);

            if (rst == null && inv.argsAsMap().containsKey(key) == false) {
                throw new EvaluationException("Missing tmpl parameter: " + key);
            }

            return rst;
        }
    }
}