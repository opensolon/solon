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
package org.noear.solon.data.sqlink.integration.datasource;

import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Solon环境下的数据源管理器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SolonDataSourceManager implements DataSourceManager {
    /**
     * 数据源
     */
    private DataSource dataSource;

    public SolonDataSourceManager() {
    }

    /**
     * 获取连接
     */
    @Override
    public Connection getConnection() throws SQLException {
        return TranUtils.getConnection(getDataSource());
    }

    /**
     * 获取数据源
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 设置数据源
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
