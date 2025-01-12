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
package org.noear.solon.data.sqlink.base.transaction;

/**
 * 事务管理器
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface TransactionManager {
    /**
     * 获取事务控制器
     *
     * @param isolationLevel 事务隔离级别
     */
    Transaction get(Integer isolationLevel);

    /**
     * 移除事务控制器
     */
    void remove();

    /**
     * 获取当前事务控制器
     */
    Transaction getCurTransaction();

    /**
     * 判断当前线程是否在事务中
     */
    boolean currentThreadInTransaction();

    /**
     * 判断当前事务是否开启
     */
    boolean isOpenTransaction();
}
