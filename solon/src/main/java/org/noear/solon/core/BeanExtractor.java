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
package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Bean 提取器（提取函数，此类用于扩展AppContext，为其添加提取器）
 *
 * @author noear
 * @since 1.4
 */
@FunctionalInterface
public interface BeanExtractor<T extends Annotation> {
    /**
     * 提取
     * */
    void doExtract(BeanWrap bw, Method method, T anno) throws Throwable;
}
