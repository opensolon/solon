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
package org.noear.solon.data.sqlink.integration.transaction;

import org.noear.solon.data.sqlink.base.transaction.DefaultTransaction;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Solon环境下的事务
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SolonTransaction extends DefaultTransaction {
    public SolonTransaction(Integer isolationLevel, DataSource dataSource, TransactionManager manager) {
        super(isolationLevel, dataSource, manager);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = TranUtils.getConnection(dataSource);
        }
        connection.setAutoCommit(false);
        if (isolationLevel != null) connection.setTransactionIsolation(isolationLevel);
        return connection;
    }
}
