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
package org.noear.solon.flow.core;

import org.noear.solon.lang.Preview;

/**
 * 流驱动器
 *
 * @author noear
 * @since 3.0
 * */
@Preview("3.0")
public interface ChainDriver {
    /**
     * 处理连线条件
     */
    boolean handleCondition(ChainContext context, Condition condition) throws Throwable;

    /**
     * 处理执行任务
     */
    void handleTask(ChainContext context, Task task) throws Throwable;
}