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
package org.noear.solon.data.sql;

import org.noear.solon.data.sql.impl.SimpleSqlExecutor;
import org.noear.solon.data.sql.impl.SimpleSqlUtils;

import javax.sql.DataSource;

/**
 * Sql 工具类工厂
 *
 * @author noear
 * @since 3.0
 * */
public interface SqlUtilsFactory {
    /**
     * 创建 Sql 工具类
     *
     * @deprecated 3.0
     */
    @Deprecated
    default SqlUtils create(DataSource ds) {
        return new SimpleSqlUtils(ds);
    }

    /**
     * 创建 Sql 执行器
     *
     * @since 3.0
     */
    default SqlExecutor create(DataSource ds, String sql, Object... args) {
        return new SimpleSqlExecutor(ds, sql, args);
    }
}
