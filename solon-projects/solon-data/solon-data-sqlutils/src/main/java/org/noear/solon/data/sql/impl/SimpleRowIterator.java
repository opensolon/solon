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

import org.noear.solon.data.sql.bound.RowConverter;
import org.noear.solon.data.sql.bound.RowIterator;

import java.sql.SQLException;

/**
 * 行遍历器简单实现
 *
 * @author noear
 * @since 3.0
 */
class SimpleRowIterator<T> implements RowIterator<T> {
    private final CommandHolder holder;
    private final RowConverter<T> converter;

    public SimpleRowIterator(CommandHolder holder, RowConverter<T> converter) {
        this.holder = holder;
        this.converter = converter;
    }

    //当前行
    private T rowCurrent;
    //行号
    private int rowNum = 0;

    @Override
    public boolean hasNext() {
        try {
            if (holder.rsts.next()) {
                rowCurrent = converter.convert(holder.rsts);
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
    public T next() {
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