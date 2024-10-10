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
import org.noear.solon.data.sql.RowIterator;

import java.sql.SQLException;

/**
 * 行遍历器简单实现
 *
 * @author noear
 * @since 3.0
 */
class SimpleRowIterator implements RowIterator {
    private final CommandHolder holder;

    public SimpleRowIterator(CommandHolder holder) {
        this.holder = holder;
    }

    //当前行
    private Row rowCurrent;
    //行号
    private int rowNum = 0;

    @Override
    public boolean hasNext() {
        try {
            if (holder.rsts.next()) {
                rowCurrent = holder.getRow();
                rowNum++;
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
    public long rownum() {
        return rowNum;
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