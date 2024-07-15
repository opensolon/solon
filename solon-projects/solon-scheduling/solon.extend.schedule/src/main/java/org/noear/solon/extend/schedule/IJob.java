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
package org.noear.solon.extend.schedule;

/**
 * 任务
 *
 * @author noear
 * @since 1.0
 * */
public interface IJob {
    /**
     * 间格时间（单位：毫秒）
     */
    int getInterval();

    /**
     * 执行代码
     */
    void exec() throws Throwable;

    /**
     * 延迟（单位：毫秒）
     * */
    default int getDelay() {
        return 0;
    }


    /**
     * 任务名称，可能由bean确定
     */
    default String getName() {
        return null;
    }

    /**
     * 执行线程数
     */
    default int getThreads() {
        return 1;
    }
}
