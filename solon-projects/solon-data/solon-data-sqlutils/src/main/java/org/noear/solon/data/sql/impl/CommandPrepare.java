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
package org.noear.solon.data.sql.impl;

import org.noear.solon.core.util.RunUtil;

import java.io.Closeable;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 指令预处理
 *
 * @author noear
 * @since 3.0
 */
class CommandPrepare implements Closeable {
    public Connection conn = null;
    public PreparedStatement stmt = null;
    public ResultSet rsts = null;

    private final SqlUtilsImpl _utils;

    public CommandPrepare(SqlUtilsImpl utils) {
        _utils = utils;
    }


    private ResultSetMetaData rowMeta;

    public Map<String, Object> getRow() throws SQLException {
        if (rowMeta == null) {
            rowMeta = rsts.getMetaData();
        }

        Map<String, Object> row = new LinkedHashMap<>();
        for (int i = 1; i <= rowMeta.getColumnCount(); i++) {
            row.put(rowMeta.getColumnLabel(i), _utils.getObject(this, i));
        }

        return row;
    }

    @Override
    public void close() {
        if (rsts != null) {
            RunUtil.runAndTry(rsts::close);
            rsts = null;
        }

        if (stmt != null) {
            RunUtil.runAndTry(stmt::close);
            stmt = null;
        }

        if (conn != null) {
            RunUtil.runAndTry(conn::close);
            conn = null;
        }
    }
}