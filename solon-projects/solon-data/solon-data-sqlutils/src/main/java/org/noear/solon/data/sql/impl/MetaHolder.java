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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 元信息持有人
 *
 * @author noear
 * @since 3.0
 */
class MetaHolder {
    public final ResultSetMetaData meta;
    public final int size;

    public MetaHolder(ResultSetMetaData meta) throws SQLException {
        this.meta = meta;
        this.size = meta.getColumnCount();
    }

    /**
     * 获取名字
     */
    public String getName(int columnIdx) throws SQLException {
        return meta.getColumnLabel(columnIdx);
    }

    private Map<String, Integer> namesIdx;

    /**
     * 获取名字的列顺位
     */
    public int getNameColumnIdx(String name) throws SQLException {
        if (namesIdx == null) {
            namesIdx = new HashMap<>();

            for (int i = 1; i <= size; i++) {
                namesIdx.put(getName(i), i);
            }
        }

        Integer tmp = namesIdx.get(name);

        if (tmp == null) {
            return -1;
        } else {
            return tmp;
        }
    }
}