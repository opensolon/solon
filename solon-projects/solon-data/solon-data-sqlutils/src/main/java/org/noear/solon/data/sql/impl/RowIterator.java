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

import org.noear.solon.data.sql.Row;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * 行遍历器
 *
 * @author noear
 * @since 3.0
 */
public class RowIterator implements Iterator<Row>, Closeable {
    private final CommandHolder holder;

    public RowIterator(CommandHolder holder) {
        this.holder = holder;
    }

    //当前行
    private Row rowCurrent;

    @Override
    public boolean hasNext() {
        try {
            if (holder.rsts.next()) {
                rowCurrent = holder.getRow();
            } else {
                rowCurrent = null;
            }

            return rowCurrent != null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Row next() {
        return rowCurrent;
    }

    @Override
    public void remove() {
        this.close();
    }

    @Override
    public void close() {
        holder.close();
    }
}