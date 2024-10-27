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
package org.noear.solon.data.sqlink.core.api.crud.create;

import org.noear.solon.data.sqlink.base.IConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class ObjectInsert<T> extends InsertBase
{
    private final List<T> tObjects = new ArrayList<>();
    private final Class<T> tableType;

    public ObjectInsert(IConfig config, Class<T> tableType)
    {
        super(config);
        this.tableType = tableType;
    }

    public ObjectInsert<T> insert(T t)
    {
        tObjects.add(t);
        return this;
    }

    public ObjectInsert<T> insert(Collection<T> ts)
    {
        tObjects.addAll(ts);
        return this;
    }

    @Override
    protected List<T> getObjects()
    {
        return tObjects;
    }

    @Override
    protected Class<T> getTableType()
    {
        return tableType;
    }
}
