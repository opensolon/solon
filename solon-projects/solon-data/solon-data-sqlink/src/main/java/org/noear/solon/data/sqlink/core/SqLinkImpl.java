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
package org.noear.solon.data.sqlink.core;

import io.github.kiryu1223.expressionTree.expressions.annos.Recode;
import org.noear.solon.data.sqlink.SqLink;
import org.noear.solon.data.sqlink.api.crud.create.ObjectInsert;
import org.noear.solon.data.sqlink.api.crud.delete.LDelete;
import org.noear.solon.data.sqlink.api.crud.read.Empty;
import org.noear.solon.data.sqlink.api.crud.read.EmptyQuery;
import org.noear.solon.data.sqlink.api.crud.read.LQuery;
import org.noear.solon.data.sqlink.api.crud.update.LUpdate;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.transaction.Transaction;
import org.noear.solon.data.sqlink.core.exception.SqLinkException;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.visitor.ExpressionUtil;

import java.util.Collection;

/**
 * 发起数据库操作接口实现
 *
 * @author kiryu1223
 * @since 3.0
 */
class SqLinkImpl implements SqLink {
    private final SqLinkConfig config;

    SqLinkImpl(SqLinkConfig config) {
        this.config = config;
    }

    /**
     * 手动开始事务
     *
     * @param isolationLevel 事务级别
     * @return 事务对象
     */
    @Override
    public Transaction beginTransaction(Integer isolationLevel) {
        return config.getTransactionManager().get(isolationLevel);
    }

    /**
     * 手动开始事务
     *
     * @return 事务对象
     */
    @Override
    public Transaction beginTransaction() {
        return beginTransaction(null);
    }

    /**
     * 查询
     *
     * @param c   数据类类对象
     * @param <T> 数据类类型
     * @return 查询过程对象
     */
    @Override
    public <T> LQuery<T> query(@Recode Class<T> c) {
        String asName = ExpressionUtil.getAsName(c);
        return new LQuery<>(new QuerySqlBuilder(config, config.getSqlExpressionFactory().queryable(c,asName)));
    }

    /**
     * 进行不包含表的查询
     *
     * @return 查询过程对象
     */
    @Override
    public EmptyQuery queryEmptyTable() {
        return new EmptyQuery(new QuerySqlBuilder(config, config.getSqlExpressionFactory().queryable(Empty.class,"")));
    }

    /**
     * 新增
     *
     * @param t   数据类对象
     * @param <T> 数据类类型
     * @return 新增过程对象
     */
    @Override
    public <T> ObjectInsert<T> insert(@Recode T t) {
        ObjectInsert<T> objectInsert = new ObjectInsert<>(config, (Class<T>) t.getClass());
        return objectInsert.insert(t);
    }

    /**
     * 集合新增
     *
     * @param ts  数据类对象集合
     * @param <T> 数据类类型
     * @return 新增过程对象
     */
    @Override
    public <T> ObjectInsert<T> insert(@Recode Collection<T> ts) {
        ObjectInsert<T> objectInsert = new ObjectInsert<>(config, getType(ts));
        return objectInsert.insert(ts);
    }

    /**
     * 更新
     *
     * @param c   数据类类对象
     * @param <T> 数据类类型
     * @return 更新过程对象
     */
    @Override
    public <T> LUpdate<T> update(@Recode Class<T> c) {
        return new LUpdate<>(config, c);
    }

    /**
     * 删除
     *
     * @param c   数据类类对象
     * @param <T> 数据类类型
     * @return 删除过程对象
     */
    public <T> LDelete<T> delete(@Recode Class<T> c) {
        return new LDelete<>(config, c);
    }


    @Override
    public SqLinkConfig getConfig() {
        return config;
    }

    private <T> Class<T> getType(Collection<T> ts) {
        for (T t : ts) {
            return (Class<T>) t.getClass();
        }
        throw new SqLinkException("insert内容为空");
    }
}