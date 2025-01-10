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
package org.noear.solon.flow;

import java.util.List;

/**
 * 链上下文
 *
 * @author noear
 * @since 3.0
 * */
public interface ChainContext {
    //是否取消流程
    boolean is_cancel();

    //条件处理
    boolean condition_handle(Condition condition) throws Exception;

    //任务处理
    void task_handle(List<NodeTask> tasks) throws Exception;
}
