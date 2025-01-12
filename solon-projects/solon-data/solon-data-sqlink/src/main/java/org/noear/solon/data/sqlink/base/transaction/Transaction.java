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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务控制器
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface Transaction extends AutoCloseable {
    /**
     * 提交事务
     */
    void commit();

    /**
     * 回滚事务
     */
    void rollback();

    /**
     * 关闭事务
     */
    @Override
    void close();

    /**
     * 获取连接对象
     */
    Connection getConnection() throws SQLException;

    /**
     * 获取事务级别
     */
    Integer getIsolationLevel();
}
