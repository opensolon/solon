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
import java.lang.reflect.Method;

/**
 * 可执行的实现
 *
 * @author noear
 * @since 3.0
 */
public class ExecableImpl implements Execable {
    private final Method method;

    public ExecableImpl(Class<?> clazz) {
        this.method = clazz.getDeclaredMethods()[0];
    }

    /**
     * 获取方法
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 执行
     */
    public Object exec(Object... args) throws InvocationTargetException {
        try {
            return method.invoke(null, args);
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }
}