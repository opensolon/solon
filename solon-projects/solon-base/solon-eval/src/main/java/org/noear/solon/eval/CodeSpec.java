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

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 代码申明
 *
 * @author noear
 * @since 3.0
 */
public class CodeSpec {
    private final String code;
    private Class<?>[] imports;
    private Map.Entry<String, Class<?>>[] parameters;
    private Class<?> returnType;

    public CodeSpec(String code) {
        this.code = code;
    }

    /**
     * 配置导入
     */
    public CodeSpec imports(Class<?>... imports) {
        this.imports = imports;
        return this;
    }

    /**
     * 配置参数申明
     */
    public CodeSpec parameters(Map.Entry<String, Class<?>>... parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * 配置返回类型
     */
    public CodeSpec returnType(Class<?> returnType) {
        this.returnType = returnType;
        return this;
    }

    //////////////////

    /**
     * 获取代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取导入
     */
    public Class<?>[] getImports() {
        return imports;
    }

    /**
     * 获取参数申明
     */
    public Map.Entry<String, Class<?>>[] getParameters() {
        return parameters;
    }

    /**
     * 获取返回类型
     */
    public Class<?> getReturnType() {
        return returnType;
    }

    //////////////////

    private boolean deepEquals(Map.Entry<String, Class<?>>[] a, Map.Entry<String, Class<?>>[] b) {
        if (a == b)
            return true;
        else if (a == null || b == null)
            return false;
        else if (a.length != b.length)
            return false;
        else {
            for (int i = 0; i < a.length; i++) {
                if (Objects.equals(a[i], b[i]) == false) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeSpec)) return false;
        CodeSpec codeSpec = (CodeSpec) o;
        return Objects.equals(code, codeSpec.code) && this.deepEquals(parameters, codeSpec.parameters);
    }


    @Override
    public int hashCode() {
        return Objects.hash(code, Arrays.hashCode(parameters));
    }
}