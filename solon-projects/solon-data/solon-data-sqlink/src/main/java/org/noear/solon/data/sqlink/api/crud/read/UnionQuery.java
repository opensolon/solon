package org.noear.solon.data.sqlink.api.crud.read;

import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import org.noear.solon.data.sqlink.api.crud.CRUD;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlUnionsExpression;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.session.SqlSession;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeFactory;
import org.noear.solon.data.sqlink.base.toBean.build.ObjectBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import org.noear.solon.data.sqlink.core.sqlBuilder.UnionBuilder;
import org.noear.solon.data.sqlink.core.visitor.SqlVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnionQuery<T> extends CRUD {
    private static final Logger log = LoggerFactory.getLogger(UnionQuery.class);
    private final UnionBuilder unionBuilder;

    public UnionQuery(SqLinkConfig config, LQuery<T> q1, LQuery<T> q2, boolean all) {
        this(config, q1.getSqlBuilder().getQueryable(), q2.getSqlBuilder().getQueryable(), all);
    }

    public UnionQuery(SqLinkConfig config, EndQuery<T> q1, EndQuery<T> q2, boolean all) {
        this(config, q1.getSqlBuilder().getQueryable(), q2.getSqlBuilder().getQueryable(), all);
    }

    public UnionQuery(SqLinkConfig config, ISqlQueryableExpression q1, ISqlQueryableExpression q2, boolean all) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlUnionsExpression unions = factory.unions();
        unions.addUnion(factory.union(q2, all));
        unionBuilder = new UnionBuilder(config, q1, unions, factory.orderBy(), factory.limit());
    }

    protected SqLinkConfig getConfig() {
        return unionBuilder.getConfig();
    }

    // region [UNION]

    public UnionQuery<T> union(LQuery<T> query, boolean all) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        ISqlQueryableExpression queryable = query.getSqlBuilder().getQueryable();
        unionBuilder.addUnion(factory.union(queryable, all));
        return this;
    }

    public UnionQuery<T> union(LQuery<T> query) {
        return union(query, false);
    }

    public UnionQuery<T> unionAll(LQuery<T> query) {
        return union(query, true);
    }

    public UnionQuery<T> union(EndQuery<T> query, boolean all) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        ISqlQueryableExpression queryable = query.getSqlBuilder().getQueryable();
        unionBuilder.addUnion(factory.union(queryable, all));
        return this;
    }

    public UnionQuery<T> union(EndQuery<T> query) {
        return union(query, false);
    }

    public UnionQuery<T> unionAll(EndQuery<T> query) {
        return union(query, true);
    }

    // endregion

    // region [ORDER BY]

    /**
     * 设置orderBy的字段以及升降序，多次调用可以指定多个orderBy字段<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回需要的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param asc  是否为升序
     * @return this
     */
    public <R> UnionQuery<T> orderBy(@Expr(Expr.BodyType.Expr) Func1<T, R> expr, boolean asc) {
        throw new NotCompiledException();
    }

    public <R> UnionQuery<T> orderBy(ExprTree<Func1<T, R>> expr, boolean asc) {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        SqlVisitor sqlVisitor = new SqlVisitor(getConfig());
        ISqlExpression expression = sqlVisitor.visit(expr.getTree());
        unionBuilder.addOrder(factory.order(expression, asc));
        return this;
    }

    /**
     * 设置orderBy的字段并且为升序，多次调用可以指定多个orderBy字段<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回需要的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public <R> UnionQuery<T> orderBy(@Expr(Expr.BodyType.Expr) Func1<T, R> expr) {
        throw new NotCompiledException();
    }

    public <R> UnionQuery<T> orderBy(ExprTree<Func1<T, R>> expr) {
        return orderBy(expr, true);
    }

    // endregion

    // region [LIMIT]

    /**
     * 获取指定数量的数据
     *
     * @param rows 需要返回的条数
     * @return this
     */
    public UnionQuery<T> limit(long rows) {
        unionBuilder.addLimit(0, rows);
        return this;
    }

    /**
     * 跳过指定数量条数据，再指定获取指定数量的数据
     *
     * @param offset 需要跳过的条数
     * @param rows   需要返回的条数
     * @return this
     */
    public UnionQuery<T> limit(long offset, long rows) {
        unionBuilder.addLimit(offset, rows);
        return this;
    }

    // endregion

    // region [SQL]

    public String toSql() {
        return unionBuilder.getSql();
    }

    public List<T> toList() {
        SqLinkConfig config = getConfig();
        List<SqlValue> sqlValues = new ArrayList<>();
        boolean single = unionBuilder.isSingle();
        List<FieldMetaData> mappingData = single ? Collections.emptyList() : unionBuilder.getMappingData();
        String sql = unionBuilder.getSqlAndValue(sqlValues);
        tryPrintSql(log, sql);
        Class<T> targetClass = unionBuilder.getTargetClass();
        SqlSession session = config.getSqlSessionFactory().getSession(config);
        return session.executeQuery(
                r -> ObjectBuilder.start(r, targetClass, mappingData, single, config).createList(),
                sql,
                sqlValues
        );
    }

    // endregion
}
