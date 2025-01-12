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
package org.noear.solon.scheduling.retry;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 被调用者
 *
 * @author noear
 * @since 2.4
 */
public interface Callee {
    /**
     * 被调目标
     */
    Object target();

    /**
     * 被调函数
     */
    Method method();

    /**
     * 参数
     */
    Object args();

    /**
     * 参数 map 形式
     */
    Map<String, Object> argsAsMap();

    /**
     * 调用
     */
    Object call() throws Throwable;
}
