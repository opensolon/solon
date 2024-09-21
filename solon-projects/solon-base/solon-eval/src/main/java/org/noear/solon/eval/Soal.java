/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.eval;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 评估器工具（Solon Evaluator）
 *
 * @author noear
 * @since 3.0
 */
public interface Soal {
    /**
     * 获取实例
     */
    static EvaluatorInstance getInstance() {
        return EvaluatorInstance.getInstance();
    }

    /**
     * 编译
     *
     * @param code 代码
     */
    static Execable compile(String code) {
        return compile(new CodeSpec(code));
    }

    /**
     * 编译
     *
     * @param codeSpec 代码申明
     */
    static Execable compile(CodeSpec codeSpec) {
        return EvaluatorInstance.getInstance().compile(codeSpec);
    }


    /**
     * 评估
     *
     * @param code 代码
     */
    static Object eval(String code) throws InvocationTargetException {
        return eval(new CodeSpec(code));
    }

    /**
     * 评估
     *
     * @param codeSpec 代码申明
     * @param args     执行参数
     */
    static Object eval(CodeSpec codeSpec, Object... args) throws InvocationTargetException {
        return EvaluatorInstance.getInstance().eval(codeSpec, args);
    }

    /**
     * 评估
     *
     * @param code 代码
     */
    static Object eval(String code, Map<String, Object> context) throws InvocationTargetException {
        assert context != null;

        ParamSpec[] parameters = new ParamSpec[context.size()];
        Object[] args = new Object[context.size()];

        int idx = 0;
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            parameters[idx] = new ParamSpec(entry.getKey(), entry.getValue().getClass());
            args[idx] = entry.getValue();
        }

        return eval(new CodeSpec(code).parameters(parameters), args);
    }
}