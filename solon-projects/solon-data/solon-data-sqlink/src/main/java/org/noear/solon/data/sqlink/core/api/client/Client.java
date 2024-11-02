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
package org.noear.solon.data.sqlink.core.api.client;

import org.noear.solon.data.sqlink.core.api.crud.create.ObjectInsert;
import org.noear.solon.data.sqlink.core.api.crud.delete.LDelete;
import org.noear.solon.data.sqlink.core.api.crud.read.EmptyQuery;
import org.noear.solon.data.sqlink.core.api.crud.read.LQuery;
import org.noear.solon.data.sqlink.core.api.crud.update.LUpdate;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.transaction.Transaction;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;
import org.noear.solon.data.sqlink.core.exception.SQLinkException;

import java.util.Collection;

/**
 * 发起数据库操作的对象
 *
 * @author kiryu1223
 * @since 3.0
 */
public class Client
{
    private final IConfig config;

    public Client(IConfig config)
    {
        this.config = config;
    }

    public void useDs(String key)
    {
        config.getDataSourceManager().useDs(key);
    }

    public void useDefDs()
    {
        config.getDataSourceManager().useDefDs();
    }

    /**
     * 手动开始事务
     * @param isolationLevel 事务级别
     * @return 事务对象
     */
    public Transaction beginTransaction(Integer isolationLevel)
    {
        return config.getTransactionManager().get(isolationLevel);
    }

    /**
     * 手动开始事务
     * @return 事务对象
     */
    public Transaction beginTransaction()
    {
        return beginTransaction(null);
    }

    /**
     * 查询
     *
     * @param c 数据类类对象
     * @return 查询过程对象
     * @param <T> 数据类类型
     */
    public <T> LQuery<T> query(@Recode Class<T> c)
    {
        return new LQuery<>(config, c);
    }

    /**
     * 进行不包含表的查询
     * @return 查询过程对象
     */
    public EmptyQuery queryEmptyTable()
    {
        return new EmptyQuery(config);
    }

    /**
     * 新增
     * @param t 数据类对象
     * @return 新增过程对象
     * @param <T> 数据类类型
     */
    public <T> ObjectInsert<T> insert(@Recode T t)
    {
        ObjectInsert<T> objectInsert = new ObjectInsert<>(config, (Class<T>) t.getClass());
        return objectInsert.insert(t);
    }

    /**
     * 集合新增
     * @param ts 数据类对象集合
     * @return 新增过程对象
     * @param <T> 数据类类型
     */
    public <T> ObjectInsert<T> insert(@Recode Collection<T> ts)
    {
        ObjectInsert<T> objectInsert = new ObjectInsert<>(config, getType(ts));
        return objectInsert.insert(ts);
    }

    /**
     * 更新
     * @param c 数据类类对象
     * @return 更新过程对象
     * @param <T> 数据类类型
     */
    public <T> LUpdate<T> update(@Recode Class<T> c)
    {
        return new LUpdate<>(config, c);
    }

    /**
     * 删除
     * @param c 数据类类对象
     * @return 删除过程对象
     * @param <T> 数据类类型
     */
    public <T> LDelete<T> delete(@Recode Class<T> c)
    {
        return new LDelete<>(config, c);
    }

    private <T> Class<T> getType(Collection<T> ts)
    {
        for (T t : ts)
        {
            return (Class<T>) t.getClass();
        }
        throw new SQLinkException("insert内容为空");
    }

    public IConfig getConfig()
    {
        return config;
    }
}
