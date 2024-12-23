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
package org.noear.solon.data.rx.sql.impl;

import io.r2dbc.spi.ConnectionFactory;
import org.noear.solon.data.rx.sql.RxSqlExecutor;
import org.noear.solon.data.rx.sql.RxSqlUtils;
import org.noear.solon.data.rx.sql.SqlConfiguration;

/**
 * Sql 工具类简单实现
 *
 * @author noear
 * @since 3.0
 */
public class DefaultRxSqlUtils implements RxSqlUtils {
    private final ConnectionFactory ds;

    public DefaultRxSqlUtils(ConnectionFactory ds) {
        this.ds = ds;
    }

    @Override
    public RxSqlExecutor sql(String sql, Object... args) {
        return SqlConfiguration.getFactory().create(ds, sql, args);
    }
}