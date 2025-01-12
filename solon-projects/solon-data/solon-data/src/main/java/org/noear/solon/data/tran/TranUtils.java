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

import org.noear.solon.Solon;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.core.util.RunnableEx;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务工具
 *
 * @author noear
 * @since 1.0
 * @since 2.5
 * */
public final class TranUtils {
    private static TranExecutor executor = TranExecutorDefault.global;

    static {
        Solon.context().getBeanAsync(TranExecutor.class, bean -> executor = bean);
    }

    /**
     * 执行事务
     */
    public static void execute(Tran tran, RunnableEx runnable) throws Throwable {
        executor.execute(tran, runnable);
    }

    /**
     * 是否在事务中
     */
    public static boolean inTrans() {
        return executor.inTrans();
    }

    /**
     * 是否在事务中且只读
     */
    public static boolean inTransAndReadOnly() {
        return executor.inTransAndReadOnly();
    }

    /**
     * 监听事务
     *
     * @since 2.5
     */
    public static void listen(TranListener listener) throws IllegalStateException {
        executor.listen(listener);
    }

    /**
     * 获取链接
     */
    public static Connection getConnection(DataSource ds) throws SQLException {
        return executor.getConnection(ds);
    }

    /**
     * 获取链接代理（一般，用于第三方框架事务对接）
     */
    public static Connection getConnectionProxy(DataSource ds) throws SQLException {
        return new ConnectionProxy(executor.getConnection(ds));
    }

    /**
     * 获取数据源代理（一般，用于第三方框架事务对接）
     */
    public static DataSource getDataSourceProxy(DataSource ds) {
        if (ds instanceof DataSourceProxy) {
            return ds;
        } else {
            return new DataSourceProxy(ds);
        }
    }
}
