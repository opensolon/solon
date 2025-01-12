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

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.core.util.RunnableEx;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务执行器
 *
 * @author noear
 * @since 1.0
 * */
public interface TranExecutor {
    /**
     * 是否在事务中
     */
    boolean inTrans();

    /**
     * 是否在事务中且只读
     */
    default boolean inTransAndReadOnly() {
        return false;
    }

    /**
     * 获取链接
     *
     * @param ds 数据源
     */
    default Connection getConnection(DataSource ds) throws SQLException {
        return ds.getConnection();
    }

    /**
     * 监听
     *
     * @param listener 监听器
     */
    void listen(TranListener listener) throws IllegalStateException;

    /**
     * 执行
     *
     * @since 1.9
     */
    default void execute(Tran meta, RunnableEx runnable) throws Throwable {

    }
}
