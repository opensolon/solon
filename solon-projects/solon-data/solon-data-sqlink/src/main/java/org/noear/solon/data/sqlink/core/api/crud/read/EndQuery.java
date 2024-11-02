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
package org.noear.solon.data.sqlink.core.api.crud.read;

import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;

import java.util.List;

/**
 * 终结查询过程
 *
 * @author kiryu1223
 * @since 3.0
 */
public class EndQuery<T> extends QueryBase
{
    public EndQuery(QuerySqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    /**
     * 检查表中是否存在至少一条数据
     *
     * @return boolean
     */
    @Override
    public boolean any()
    {
        return super.any();
    }

    /**
     * list集合形式返回数据，无数据则返回空list
     *
     * @return List
     */
    @Override
    public List<T> toList()
    {
        return super.toList();
    }

    /**
     * 返回一条数据，会调用各种数据库limit 1的具体实现，无数据则返回null
     *
     * @return T
     */
    @Override
    public T first()
    {
        return super.first();
    }
}
