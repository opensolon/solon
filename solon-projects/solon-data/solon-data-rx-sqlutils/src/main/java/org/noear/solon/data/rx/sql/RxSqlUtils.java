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
package org.noear.solon.data.rx.sql;

import io.r2dbc.spi.ConnectionFactory;
import org.noear.solon.Solon;
import org.noear.solon.lang.Preview;
import org.noear.solon.data.rx.sql.impl.DefaultRxSqlUtils;

/**
 * Sql 工具类（线程安全，可作为单例保存）
 *
 * @author noear
 * @since 3.0
 */
@Preview("3.0")
public interface RxSqlUtils {
    static RxSqlUtils of(ConnectionFactory ds) {
        assert ds != null;
        return new DefaultRxSqlUtils(ds);
    }

    static RxSqlUtils ofName(String dsName) {
        return of(Solon.context().getBean(dsName));
    }

    /**
     * 执行代码
     *
     * @param sql  代码
     * @param args 参数
     */
    RxSqlExecutor sql(String sql, Object... args);

    /**
     * 执行代码
     *
     * @param sqlSpec 代码申明
     */
    default RxSqlExecutor sql(SqlSpec sqlSpec) {
        return sql(sqlSpec.getSql(), sqlSpec.getArgs());
    }
}