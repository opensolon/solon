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
 * @deprecated 3.0
 */
@Deprecated
class MetaHolder {
    private final String[] names;

    public final int size;

    public MetaHolder(ResultSetMetaData meta) throws SQLException {
        this.size = meta.getColumnCount();
        this.names = new String[size];

        for (int cI = 1; cI <= size; cI++) {
            names[cI - 1] = meta.getColumnLabel(cI);
        }
    }

    /**
     * 获取名字
     */
    public String getName(int columnIdx) {
        return names[columnIdx - 1];
    }

    private Map<String, Integer> namesIdx;

    /**
     * 获取名字的列顺位
     */
    public int getNameColumnIdx(String name) {
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