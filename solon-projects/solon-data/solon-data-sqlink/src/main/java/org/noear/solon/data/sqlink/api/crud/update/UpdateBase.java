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
package org.noear.solon.data.sqlink.api.crud.update;

import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.LambdaExpression;
import org.noear.solon.data.sqlink.api.crud.CRUD;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.session.SqlSession;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.core.exception.SqLinkException;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.core.visitor.SqlVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class UpdateBase extends CRUD {
    public final static final Logger log = LoggerFactory.getLogger(UpdateBase.class);

    private final UpdateSqlBuilder sqlBuilder;

    public UpdateBase(UpdateSqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    protected UpdateSqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    protected SqLinkConfig getConfig() {
        return sqlBuilder.getConfig();
    }

    public String toSql() {
        return sqlBuilder.getSql();
    }

    /**
     * 执行sql语句
     *
     * @return 执行后的结果
     */
    public long executeRows() {
        checkHasSet();
        checkHasWhere();
        SqLinkConfig config = getConfig();
        List<SqlValue> sqlValues = new ArrayList<>();
        String sql = sqlBuilder.getSqlAndValue(sqlValues);
        //tryPrintUseDs(log, config.getDataSourceManager().getDsKey());
        tryPrintSql(log, sql);
        SqlSession session = config.getSqlSessionFactory().getSession(config);
        return session.executeUpdate(sql, sqlValues);
    }

    private void checkHasSet() {
        if (!sqlBuilder.hasSet()) {
            throw new SqLinkException("SET为空");
        }
    }

    private void checkHasWhere() {
        if (getConfig().isIgnoreUpdateNoWhere()) return;
        if (!sqlBuilder.hasWhere()) {
            throw new SqLinkException("UPDATE没有条件");
        }
    }

    protected void join(JoinType joinType, Class<?> target, ExprTree<?> expr) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        SqlVisitor sqlVisitor = new SqlVisitor(getConfig(),sqlBuilder.getUpdate());
        ISqlExpression on = sqlVisitor.visit(expr.getTree());
        ISqlTableExpression table = factory.table(target);
        getSqlBuilder().addJoin(joinType, table, on);
    }

    protected void set(LambdaExpression<?> left, Object value) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        SqlVisitor sqlVisitor = new SqlVisitor(getConfig(),sqlBuilder.getUpdate());
        ISqlColumnExpression column = sqlVisitor.toColumn(left);
        sqlBuilder.addSet(factory.set(column, factory.AnyValue(value)));
    }

    protected void set(LambdaExpression<?> left, LambdaExpression<?> right) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        SqlVisitor sqlVisitor = new SqlVisitor(getConfig(),sqlBuilder.getUpdate());
        ISqlColumnExpression column = sqlVisitor.toColumn(left);
        ISqlExpression value = sqlVisitor.visit(right);
        sqlBuilder.addSet(factory.set(column, value));
    }

    protected void where(LambdaExpression<?> lambda) {
        SqlVisitor sqlVisitor = new SqlVisitor(getConfig(),sqlBuilder.getUpdate());
        ISqlExpression expression = sqlVisitor.visit(lambda);
        sqlBuilder.addWhere(expression);
    }
}
