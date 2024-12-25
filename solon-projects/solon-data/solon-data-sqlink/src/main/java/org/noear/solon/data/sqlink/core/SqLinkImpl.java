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

import io.github.kiryu1223.expressionTree.delegate.Action1;
import io.github.kiryu1223.expressionTree.expressions.LambdaExpression;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;
import org.noear.solon.data.sqlink.SqLink;
import org.noear.solon.data.sqlink.api.crud.create.ObjectInsert;
import org.noear.solon.data.sqlink.api.crud.delete.LDelete;
import org.noear.solon.data.sqlink.api.crud.read.*;
import org.noear.solon.data.sqlink.api.crud.update.LUpdate;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;
import org.noear.solon.data.sqlink.base.transaction.Transaction;
import org.noear.solon.data.sqlink.core.exception.SqLinkException;
import org.noear.solon.data.sqlink.core.sqlBuilder.DeleteSqlBuilder;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.core.tuple.Tuple1;
import org.noear.solon.data.sqlink.core.tuple.Tuple2;
import org.noear.solon.data.sqlink.core.tuple.Tuple3;
import org.noear.solon.data.sqlink.core.tuple.Tuple4;
import org.noear.solon.data.sqlink.core.visitor.ExpressionUtil;

import java.util.Collection;
import java.util.Map;

/**
 * 发起数据库操作接口实现
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SqLinkImpl implements SqLink {
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
        LQuery<T> query = new LQuery<>(new QuerySqlBuilder(config, config.getSqlExpressionFactory().queryable(c, asName)));
        Action1<LQuery<T>> onSelectByType = config.getAop().getOnSelectByType(c);
        if (onSelectByType != null) {
            onSelectByType.invoke(query);
        }
        return query;
    }

    @Override
    public <T> UnionQuery<T> union(LQuery<T> q1, LQuery<T> q2) {
        return new UnionQuery<>(config, q1, q2, false);
    }

    @Override
    public <T> UnionQuery<T> union(EndQuery<T> q1, EndQuery<T> q2) {
        return new UnionQuery<>(config, q1, q2, false);
    }

    @Override
    public <T> UnionQuery<T> unionAll(LQuery<T> q1, LQuery<T> q2) {
        return new UnionQuery<>(config, q1, q2, true);
    }

    @Override
    public <T> UnionQuery<T> unionAll(EndQuery<T> q1, EndQuery<T> q2) {
        return new UnionQuery<>(config, q1, q2, false);
    }

    /**
     * 进行不包含表的查询
     *
     * @return 查询过程对象
     */
    @Override
    public EmptyQuery queryEmptyTable() {
        return new EmptyQuery(new QuerySqlBuilder(config, config.getSqlExpressionFactory().queryable(Empty.class, "")));
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
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return new LUpdate<>(new UpdateSqlBuilder(config, factory.update(c, ExpressionUtil.getAsName(c))));
    }

    /**
     * 删除
     *
     * @param c   数据类类对象
     * @param <T> 数据类类型
     * @return 删除过程对象
     */
    public <T> LDelete<T> delete(@Recode Class<T> c) {
        return new LDelete<>(new DeleteSqlBuilder(config, c));
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

    public <T> LQuery<Tuple1<T>> query(int... ins) {
        return new LQuery<>(new QuerySqlBuilder(config, config.getSqlExpressionFactory().queryable(Tuple1.class, "")));
    }

    public <T1, T2> LQuery<Tuple2<T1, T2>> query(byte... bytes) {
        return new LQuery<>(new QuerySqlBuilder(config, config.getSqlExpressionFactory().queryable(Tuple1.class, "")));
    }

    public <T1, T2, T3> LQuery<Tuple3<T1, T2, T3>> query(short... shorts) {
        return new LQuery<>(new QuerySqlBuilder(config, config.getSqlExpressionFactory().queryable(Tuple1.class, "")));
    }

    public <T1, T2, T3, T4> LQuery<Tuple4<T1, T2, T3, T4>> query(char... chars) {
        return new LQuery<>(new QuerySqlBuilder(config, config.getSqlExpressionFactory().queryable(Tuple1.class, "")));
    }
}