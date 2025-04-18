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
package org.noear.solon.data.sqlink.core.sqlBuilder;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.core.visitor.ExpressionUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.doGetAsName;
import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.getFirst;

/**
 * 删除语句生成器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class DeleteSqlBuilder implements ISqlBuilder {
    private final SqLinkConfig config;
    private final ISqlFromExpression from;
    private final ISqlJoinsExpression joins;
    private final ISqlWhereExpression wheres;
    //private final Class<?> target;
    private final Set<Integer> excludes = new HashSet<>();
    private final SqlExpressionFactory factory;
    //private final List<Class<?>> orderedClasses = new ArrayList<>();

    public DeleteSqlBuilder(SqLinkConfig config, Class<?> target) {
        this.config = config;
        //this.target = target;
        factory = config.getSqlExpressionFactory();
        this.joins = factory.Joins();
        this.wheres = factory.where();
        String first = getFirst(target);
        this.from = factory.from(factory.table(target), new AsName(first));
        //orderedClasses.add(target);
    }

    /**
     * 添加关联表
     *
     * @param joinType 关联类型
     * @param table    关联表
     * @param on       关联条件
     */
    public void addJoin(JoinType joinType, ISqlTableExpression table, ISqlExpression on) {
        String first = getFirst(table.getMainTableClass());
        Set<String> stringSet = new HashSet<>(joins.getJoins().size() + 1);
        stringSet.add(from.getAsName().getName());
        for (ISqlJoinExpression join : joins.getJoins()) {
            stringSet.add(join.getAsName().getName());
        }
        AsName asName = doGetAsName(first, stringSet);
        ISqlJoinExpression join = factory.join(
                joinType,
                table,
                on,
                asName
        );
        joins.addJoin(join);
    }

    /**
     * 添加指定删除的表
     */
//    public void addExclude(Class<?> c) {
//        excludes.add(orderedClasses.indexOf(c));
//    }

    /**
     * 添加删除的where条件
     */
    public void addWhere(ISqlExpression where) {
        wheres.addCondition(where);
    }

    @Override
    public SqLinkConfig getConfig() {
        return config;
    }

    /**
     * 是否有where条件
     */
    public boolean hasWhere() {
        return !wheres.isEmpty();
    }

    public ISqlFromExpression getFrom() {
        return from;
    }

    public ISqlJoinsExpression getJoins() {
        return joins;
    }

    @Override
    public String getSql() {
        return getSqlAndValue(null);
    }

    @Override
    public String getSqlAndValue(List<SqlValue> values) {
        SqLinkDialect disambiguation = config.getDisambiguation();
        List<String> strings = new ArrayList<>();
        strings.add("DELETE");
        strings.add(disambiguation.disambiguation(from.getAsName().getName()));
        strings.add(from.getSqlAndValue(config, values));
        if (!joins.isEmpty()) {
            strings.add(joins.getSqlAndValue(config, values));
        }
        if (!wheres.isEmpty()) {
            strings.add(wheres.getSqlAndValue(config, values));
        }
        return String.join(" ", strings);
    }
}
