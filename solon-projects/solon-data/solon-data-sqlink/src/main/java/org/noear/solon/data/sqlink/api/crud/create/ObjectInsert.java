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
package org.noear.solon.data.sqlink.api.crud.create;

import org.noear.solon.data.sqlink.base.SqLinkConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 新增过程对象
 *
 * @author kiryu1223
 * @since 3.0
 */
public class ObjectInsert<T> extends InsertBase {
    private final List<T> tObjects = new ArrayList<>();
    private final Class<T> tableType;

    public ObjectInsert(SqLinkConfig config, Class<T> tableType) {
        super(config);
        this.tableType = tableType;
    }

    /**
     * 增加一个需要新增的数据
     *
     * @param t 同类型数据对象
     * @return this
     */
    public ObjectInsert<T> insert(T t) {
        tObjects.add(t);
        return this;
    }

    /**
     * 增加多个需要新增的数据
     *
     * @param ts 同类型数据集合
     * @return this
     */
    public ObjectInsert<T> insert(Collection<T> ts) {
        tObjects.addAll(ts);
        return this;
    }

    @Override
    protected List<T> getObjects() {
        return tObjects;
    }

    @Override
    protected Class<T> getTableType() {
        return tableType;
    }
}
