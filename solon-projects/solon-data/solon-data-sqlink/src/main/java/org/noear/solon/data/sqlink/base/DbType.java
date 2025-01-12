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
package org.noear.solon.data.sqlink.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库类型
 *
 * @author kiryu1223
 * @since 3.0
 */
public enum DbType {
    Any,
    MySQL,
    SQLServer,
    H2,
    Oracle,
    SQLite,
    PostgreSQL,
    ;

    private static final Logger log = LoggerFactory.getLogger(DbType.class);

    public static DbType getByName(String dbName) {
        switch (dbName) {
            case "MySQL":
                return MySQL;
            case "Microsoft SQL Server":
                return SQLServer;
            case "H2":
                return H2;
            case "Oracle":
                return Oracle;
            case "SQLite":
                return SQLite;
            case "PostgreSQL":
                return PostgreSQL;
            default:
                log.warn("Unsupported database type:{}", dbName);
                return Any;
        }
    }
}
