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

import org.noear.snack.ONode;
import org.noear.solon.data.sql.Row;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 行简单实现
 *
 * @author noear
 * @since 3.0
 * @deprecated 3.0
 */
@Deprecated
class SimpleRow implements Row {
    private final MetaHolder _metaHolder;
    private final Object[] _data;

    public SimpleRow(MetaHolder metaHolder, Object[] data) {
        this._metaHolder = metaHolder;
        this._data = data;
    }

    @Override
    public int size() {
        return _metaHolder.size;
    }

    @Override
    public Object[] data() {
        return _data;
    }

    @Override
    public String getName(int columnIdx) {
        return _metaHolder.getName(columnIdx);
    }

    @Override
    public int getNameColumnIdx(String name) {
        return _metaHolder.getNameColumnIdx(name);
    }

    @Override
    public Object getObject(int columnIndex) {
        return _data[columnIndex - 1];
    }

    @Override
    public Object getObject(String name) {
        int idx = getNameColumnIdx(name);
        if (idx < 1) {
            throw new IllegalArgumentException("Column '" + name + "' not found");
        }

        return getObject(idx);
    }


    /**
     * 转为 Map
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int cI = 1; cI <= size(); cI++) {
            map.put(getName(cI), getObject(cI));
        }

        return map;
    }

    /**
     * 转为 Bean
     *
     * @param type 类型
     */
    @Override
    public <T> T toBean(Class<T> type) {
        return ONode.load(toMap()).toObject(type);
    }
}