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

import io.r2dbc.spi.Statement;
import org.noear.solon.data.rx.sql.bound.RxStatementBinder;

/**
 * 默认语句绑定器
 *
 * @author noear
 * @since 3.0
 */
public class DefaultRxBinder implements RxStatementBinder<Object[]> {
    @Override
    public Statement setValues(Statement stmt, Object[] args) {
        //单处理
        for (int i = 0; i < args.length; i++) {
            setObject(stmt, i, args[i]);
        }

        return stmt;
    }

    /**
     * 填充数据（为转换提供重写机会）
     *
     * @param columnIdx 列顺位（从1开始）
     */
    protected void setObject(Statement stmt, int columnIdx, Object val) {
        if (val == null) {
            stmt.bindNull(columnIdx, String.class);
        } else if (val instanceof java.util.Date) {
            if (val instanceof java.sql.Date) {
                stmt.bind(columnIdx, (java.sql.Date) val);
            } else if (val instanceof java.sql.Timestamp) {
                stmt.bind(columnIdx, (java.sql.Timestamp) val);
            } else {
                java.util.Date v1 = (java.util.Date) val;
                stmt.bind(columnIdx, new java.sql.Timestamp(v1.getTime()));
            }
        } else {
            stmt.bind(columnIdx, val);
        }
    }
}