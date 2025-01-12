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
package org.noear.solon.data.tran;

/**
 * 事务监听器
 *
 * @author noear
 * @see 2.5
 */
public interface TranListener {

    /**
     * 提交完成状态
     */
    int STATUS_COMMITTED = 0;

    /**
     * 回滚状态
     */
    int STATUS_ROLLED_BACK = 1;

    /**
     * 未知状态
     */
    int STATUS_UNKNOWN = 2;


    /**
     * 顺序位
     */
    default int getIndex() {
        return 0;
    }


    /**
     * 提交之前（可以出异常触发回滚）
     */
    default void beforeCommit(boolean readOnly) throws Throwable{
    }

    /**
     * 完成之前
     */
    default void beforeCompletion() {
    }


    /**
     * 提交之后
     */
    default void afterCommit() {
    }

    /**
     * 完成之后
     *
     * @param status 状态
     */
    default void afterCompletion(int status) {
    }
}
