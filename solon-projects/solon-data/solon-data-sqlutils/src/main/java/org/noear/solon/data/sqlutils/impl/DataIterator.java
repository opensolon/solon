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
package org.noear.solon.data.sqlutils.impl;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * 数据遍历器
 *
 * @author noear
 * @since 3.0
 */
public class DataIterator implements Iterator<Map<String,Object>>, Closeable {
    private final CommandPrepare prepare;

    public DataIterator(CommandPrepare prepare) {
        this.prepare = prepare;
    }

    private Map<String, Object> rowTmp;

    @Override
    public boolean hasNext() {
        try {
            if (prepare.rsts.next()) {
                rowTmp = prepare.getRow();
            } else {
                rowTmp = null;
            }

            return rowTmp != null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> next() {
        return rowTmp;
    }

    @Override
    public void remove() {
        this.close();
    }

    @Override
    public void close() {
        prepare.close();
    }
}