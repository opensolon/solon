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
package org.noear.solon.data.sqlink.api.crud.read;

import io.github.kiryu1223.expressionTree.delegate.Action1;
import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.delegate.Func2;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.LambdaExpression;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;
import org.noear.solon.data.sqlink.api.Result;
import org.noear.solon.data.sqlink.api.crud.delete.LDelete;
import org.noear.solon.data.sqlink.api.crud.read.group.GroupedQuery;
import org.noear.solon.data.sqlink.api.crud.read.group.Grouper;
import org.noear.solon.data.sqlink.api.crud.update.LUpdate;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.metaData.NavigateData;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import org.noear.solon.data.sqlink.core.exception.SqLinkException;
import org.noear.solon.data.sqlink.core.page.DefaultPager;
import org.noear.solon.data.sqlink.core.page.PagedResult;
import org.noear.solon.data.sqlink.core.sqlBuilder.DeleteSqlBuilder;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.core.visitor.ExpressionUtil;
import org.noear.solon.data.sqlink.core.visitor.SqlVisitor;

import java.math.BigDecimal;
import java.util.*;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.buildTree;

/**
 * 查询过程对象
 *
 * @author kiryu1223
 * @since 3.0
 */
public class LQuery<T> extends QueryBase {
    // region [INIT]

    public LQuery(QuerySqlBuilder sqlBuilder) {
        super(sqlBuilder);
    }

    // endregion

    //region [JOIN]

    protected <Tn> LQuery2<T, Tn> joinNewQuery() {
        return new LQuery2<>(getSqlBuilder());
    }

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类或查询过程
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的查询过程对象
     */
    public <Tn> LQuery2<T, Tn> innerJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> innerJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        join(JoinType.INNER, target, expr.getTree());
        return joinNewQuery();
    }

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类或查询过程
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的查询过程对象
     */
    public <Tn> LQuery2<T, Tn> innerJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> innerJoin(LQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        join(JoinType.INNER, target, expr.getTree());
        return joinNewQuery();
    }

    public <Tn> LQuery2<T, Tn> innerJoinWith(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> innerJoinWith(LQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        joinWith(JoinType.INNER, target, expr.getTree());
        return joinNewQuery();
    }

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类或查询过程
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的查询过程对象
     */
    public <Tn> LQuery2<T, Tn> leftJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> leftJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        join(JoinType.LEFT, target, expr.getTree());
        return joinNewQuery();
    }

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类或查询过程
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的查询过程对象
     */
    public <Tn> LQuery2<T, Tn> leftJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> leftJoin(LQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        join(JoinType.LEFT, target, expr.getTree());
        return joinNewQuery();
    }

    public <Tn> LQuery2<T, Tn> leftJoinWith(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> leftJoinWith(LQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        joinWith(JoinType.LEFT, target, expr.getTree());
        return joinNewQuery();
    }

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类或查询过程
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的查询过程对象
     */
    public <Tn> LQuery2<T, Tn> leftJoin(EndQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> leftJoin(EndQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        join(JoinType.LEFT, target, expr.getTree());
        return joinNewQuery();
    }

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类或查询过程
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的查询过程对象
     */
    public <Tn> LQuery2<T, Tn> rightJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> rightJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        join(JoinType.RIGHT, target, expr.getTree());
        return joinNewQuery();
    }

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类或查询过程
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的查询过程对象
     */
    public <Tn> LQuery2<T, Tn> rightJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> rightJoin(LQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        join(JoinType.RIGHT, target, expr.getTree());
        return joinNewQuery();
    }

    public <Tn> LQuery2<T, Tn> rightJoinWith(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> rightJoinWith(LQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr) {
        joinWith(JoinType.RIGHT, target, expr.getTree());
        return joinNewQuery();
    }

    //endregion

    // region [WHERE]

    /**
     * 设置where条件<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public LQuery<T> where(@Expr(Expr.BodyType.Expr) Func1<T, Boolean> func) {
        throw new NotCompiledException();
    }

    public LQuery<T> where(ExprTree<Func1<T, Boolean>> expr) {
        where(expr.getTree());
        return this;
    }

    public LQuery<T> whereIf(boolean condition, @Expr(Expr.BodyType.Expr) Func1<T, Boolean> func) {
        throw new NotCompiledException();
    }

    public LQuery<T> whereIf(boolean condition, ExprTree<Func1<T, Boolean>> expr) {
        if (condition) where(expr.getTree());
        return this;
    }

    /**
     * 设置where条件，并且以or将多个where连接<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public LQuery<T> orWhere(@Expr(Expr.BodyType.Expr) Func1<T, Boolean> func) {
        throw new NotCompiledException();
    }

    public LQuery<T> orWhere(ExprTree<Func1<T, Boolean>> expr) {
        orWhere(expr.getTree());
        return this;
    }

    public LQuery<T> orWhereIf(boolean condition, @Expr(Expr.BodyType.Expr) Func1<T, Boolean> func) {
        throw new NotCompiledException();
    }

    public LQuery<T> orWhereIf(boolean condition, ExprTree<Func1<T, Boolean>> expr) {
        if (condition) orWhere(expr.getTree());
        return this;
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
    public <R> LQuery<T> orderBy(@Expr(Expr.BodyType.Expr) Func1<T, R> expr, boolean asc) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> orderBy(ExprTree<Func1<T, R>> expr, boolean asc) {
        orderBy(expr.getTree(), asc);
        return this;
    }

    /**
     * 设置orderBy的字段并且为升序，多次调用可以指定多个orderBy字段<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回需要的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public <R> LQuery<T> orderBy(@Expr(Expr.BodyType.Expr) Func1<T, R> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> orderBy(ExprTree<Func1<T, R>> expr) {
        orderBy(expr, true);
        return this;
    }

    public <R> LQuery<T> orderByDesc(@Expr(Expr.BodyType.Expr) Func1<T, R> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> orderByDesc(ExprTree<Func1<T, R>> expr) {
        orderBy(expr, false);
        return this;
    }

    public <R> LQuery<T> orderByIf(boolean condition, @Expr(Expr.BodyType.Expr) Func1<T, R> expr, boolean asc) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> orderByIf(boolean condition, ExprTree<Func1<T, R>> expr, boolean asc) {
        if (condition) orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> LQuery<T> orderByIf(boolean condition, @Expr(Expr.BodyType.Expr) Func1<T, R> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> orderByIf(boolean condition, ExprTree<Func1<T, R>> expr) {
        if (condition) orderBy(expr, true);
        return this;
    }

    public <R> LQuery<T> orderByDescIf(boolean condition, @Expr(Expr.BodyType.Expr) Func1<T, R> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> orderByDescIf(boolean condition, ExprTree<Func1<T, R>> expr) {
        if (condition) orderBy(expr, false);
        return this;
    }

    // endregion

    // region [LIMIT]

    /**
     * 获取指定数量的数据
     *
     * @param rows 需要返回的条数
     * @return this
     */
    public LQuery<T> limit(long rows) {
        limit0(rows);
        return this;
    }

    /**
     * 跳过指定数量条数据，再指定获取指定数量的数据
     *
     * @param offset 需要跳过的条数
     * @param rows   需要返回的条数
     * @return this
     */
    public LQuery<T> limit(long offset, long rows) {
        limit0(offset, rows);
        return this;
    }

    /**
     * 获取指定数量的数据
     *
     * @param condition 条件
     * @param rows      需要返回的条数
     * @return this
     */
    public LQuery<T> limitIf(boolean condition, long rows) {
        if (condition) limit0(rows);
        return this;
    }

    /**
     * 跳过指定数量条数据，再指定获取指定数量的数据
     *
     * @param condition 条件
     * @param offset    需要跳过的条数
     * @param rows      需要返回的条数
     * @return this
     */
    public LQuery<T> limitIf(boolean condition, long offset, long rows) {
        if (condition) limit0(offset, rows);
        return this;
    }

    // endregion

    // region [GROUP BY]

    /**
     * 设置groupBy<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回一个继承于Grouper的匿名对象的lambda表达式((a) -> new Grouper(){...})，初始化段{...}内编写需要加入到Group的字段(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return 分组查询过程对象
     */
    public <Key extends Grouper> GroupedQuery<? extends Key, T> groupBy(@Expr Func1<T, Key> expr) {
        throw new NotCompiledException();
    }

    public <Key extends Grouper> GroupedQuery<? extends Key, T> groupBy(ExprTree<Func1<T, Key>> expr) {
        groupBy(expr.getTree());
        return new GroupedQuery<>(getSqlBuilder());
    }

    // endregion

    // region [SELECT]

    /**
     * 设置select，根据指定的类型的字段匹配去生成选择的sql字段
     *
     * @param r   指定的返回类型
     * @param <R> 指定的返回类型
     * @return 终结查询过程
     */
    public <R> EndQuery<R> select(@Recode Class<R> r) {
        return super.select(r);
    }

    /**
     * 设置select<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回一个继承于Result的匿名对象的lambda表达式((a) -> new Result(){...})，初始化段{...}内编写需要select的字段(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <R>  Result
     * @return 基于Result类型的新查询过程对象
     */
    public <R extends Result> LQuery<? extends R> select(@Expr(Expr.BodyType.Expr) Func1<T, R> expr) {
        throw new NotCompiledException();
    }

    public <R extends Result> LQuery<? extends R> select(ExprTree<Func1<T, R>> expr) {
        boolean single = select(expr.getTree());
        singleCheck(single);
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    /**
     * 此重载用于当想要返回某个字段的情况((r) -> r.getId),因为select泛型限制为必须是Result的子类<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回一个值的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return 终结查询过程
     */
    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func1<T, R> expr) {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func1<T, R>> expr) {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }

    public <R> LQuery<R> selectMany(@Expr(Expr.BodyType.Expr) Func1<T, Collection<R>> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery<R> selectMany(ExprTree<Func1<T, Collection<R>>> expr) {
        ISqlQueryableExpression queryable = getSqlBuilder().getQueryable();
        SqlVisitor sqlVisitor = new SqlVisitor(getConfig(), queryable);
        ISqlColumnExpression column = sqlVisitor.toColumn(expr.getTree());
        FieldMetaData fieldMetaData = column.getFieldMetaData();
        if (!fieldMetaData.hasNavigate()) {
            throw new SqLinkException("selectMany指定的字段需要被@Navigate修饰");
        }
        NavigateData navigateData = fieldMetaData.getNavigateData();
        Class<?> targetType = navigateData.getNavigateTargetType();
        FieldMetaData target = MetaDataCache.getMetaData(targetType).getFieldMetaDataByFieldName(navigateData.getTargetFieldName());
        FieldMetaData self = MetaDataCache.getMetaData(queryable.getMainTableClass()).getFieldMetaDataByFieldName(navigateData.getSelfFieldName());
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();

        AsName asName = new AsName(ExpressionUtil.getFirst(targetType));
        // 获取父的拷贝，然后把select换成自己的字段
        ISqlQueryableExpression copy = queryable.copy(getConfig());
        copy.setSelect(factory.select(Collections.singletonList(factory.column(self, copy.getFrom().getAsName())), self.getType()));
        ISqlQueryableExpression newQuery = factory.queryable(targetType, asName);
        newQuery.addWhere(factory.binary(SqlOperator.IN, factory.column(target, asName), copy));
        return new LQuery<>(new QuerySqlBuilder(getConfig(), newQuery));
    }

    // endregion

    // region [INCLUDE]

    /**
     * 对象抓取器，会根据导航属性自动为选择的字段填充属性<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回需要抓取的字段的lambda表达式，这个字段需要被Navigate修饰
     * @return 抓取过程对象
     */
    public <R> LQuery<T> include(@Expr(Expr.BodyType.Expr) Func1<T, R> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> include(ExprTree<Func1<T, R>> expr) {
        include(expr.getTree());
        return this;
    }

    /**
     * 对象抓取器，会根据导航属性自动为选择的字段填充属性,并且设置简单的过滤条件<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回需要抓取的字段的lambda表达式，这个字段需要被Navigate修饰
     * @param then 简单的过滤条件
     * @return 抓取过程对象
     */
    public <R> LQuery<T> include(@Expr(Expr.BodyType.Expr) Func1<T, R> expr, Action1<LQuery<R>> then) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> include(ExprTree<Func1<T, R>> expr, Action1<LQuery<R>> then) {
        LambdaExpression<Func1<T, R>> tree = expr.getTree();
        Class<?> targetType =tree.getReturnType();
        String first = ExpressionUtil.getFirst(targetType);
        LQuery<R> lQuery = new LQuery<>(new QuerySqlBuilder(getConfig(), getConfig().getSqlExpressionFactory().queryable(targetType, new AsName(first))));
        then.invoke(lQuery);
        QuerySqlBuilder sqlBuilder = lQuery.getSqlBuilder();
        include(tree, sqlBuilder.getQueryable());
        if (!sqlBuilder.getIncludeSets().isEmpty()) {
            getSqlBuilder().getLastIncludeSet().getIncludeSets().addAll(sqlBuilder.getIncludeSets());
        }
        return this;
    }

    /**
     * include的集合版本
     */
    public <R> LQuery<T> includes(@Expr(Expr.BodyType.Expr) Func1<T, Collection<R>> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> includes(ExprTree<Func1<T, Collection<R>>> expr) {
        include(expr.getTree());
        return this;
    }

    /**
     * include的集合版本
     */
    public <R> LQuery<T> includes(@Expr(Expr.BodyType.Expr) Func1<T, Collection<R>> expr, Action1<LQuery<R>> then) {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> includes(ExprTree<Func1<T, Collection<R>>> expr, Action1<LQuery<R>> then) {
        SqlVisitor sqlVisitor = new SqlVisitor(getConfig(), getSqlBuilder().getQueryable());
        ISqlColumnExpression column = sqlVisitor.toColumn(expr.getTree());
        Class<?> targetType = ExpressionUtil.getTargetType(column.getFieldMetaData().getGenericType());
        String first = ExpressionUtil.getFirst(targetType);
        LQuery<R> lQuery = new LQuery<>(new QuerySqlBuilder(getConfig(), getConfig().getSqlExpressionFactory().queryable(targetType, new AsName(first))));
        then.invoke(lQuery);
        QuerySqlBuilder sqlBuilder = lQuery.getSqlBuilder();
        include(expr.getTree(), sqlBuilder.getQueryable());
        if (!sqlBuilder.getIncludeSets().isEmpty()) {
            getSqlBuilder().getLastIncludeSet().getIncludeSets().addAll(sqlBuilder.getIncludeSets());
        }
        return this;
    }

    // endregion

    //region [OTHER]

    /**
     * 设置distinct
     *
     * @return this
     */
    public LQuery<T> distinct() {
        distinct0(true);
        return this;
    }

    /**
     * 设置distinct
     *
     * @param condition 是否distinct
     * @return this
     */
    public LQuery<T> distinct(boolean condition) {
        distinct0(condition);
        return this;
    }

    //endregion

    // region [toAny]

    /**
     * 返回一条数据，会调用各种数据库limit 1的具体实现，无数据则返回null
     *
     * @return T
     */
    @Override
    public T first() {
        return super.first();
    }

    /**
     * list集合形式返回数据，无数据则返回空list
     *
     * @return List
     */
    public List<T> toList() {
        return super.toList();
    }

    /**
     * list集合形式返回数据，并且执行你想要对已写入内存中的数据进行的操作，执行后再返回list
     *
     * @param func 执行操作的lambda
     * @return List
     */
    public <R> List<R> toList(Func1<T, R> func) {
        List<T> list = toList();
        List<R> rList = new ArrayList<>(list.size());
        for (T t : list) {
            rList.add(func.invoke(t));
        }
        return rList;
    }

    /**
     * Map形式返回数据，无数据则返回空Map
     *
     * @param func 指定一个key
     * @return Map
     */
    public <K> Map<K, T> toMap(Func1<T, K> func) {
        return toMap(func, new HashMap<>());
    }

    /**
     * Map形式返回数据，无数据则返回空Map
     *
     * @param func 指定一个key
     * @param map  指定你想要的Map类型
     * @return Map
     */
    public <K> Map<K, T> toMap(Func1<T, K> func, Map<K, T> map) {
        for (T t : toList()) {
            map.put(func.invoke(t), t);
        }
        return map;
    }

    /**
     * 分页返回数据，无数据则返回空List
     *
     * @param pageIndex 页编号 默认1开始
     * @param pageSize  页长度 默认大于等于1
     * @return 分页数据
     */
    public PagedResult<T> toPagedResult(long pageIndex, long pageSize) {
        return toPagedResult0(pageIndex, pageSize, DefaultPager.instance);
    }

    /**
     * 分页返回数据，无数据则返回空List
     *
     * @param pageIndex 页编号 默认1开始
     * @param pageSize  页长度 默认大于等于1
     * @return 分页数据
     */
    public PagedResult<T> toPagedResult(int pageIndex, int pageSize) {
        return toPagedResult((long) pageIndex, (long) pageSize);
    }

    /**
     * 返回树形数据(内存排序)
     */
    public List<T> toTreeList(@Expr(Expr.BodyType.Expr) Func1<T, Collection<T>> expr) {
        throw new NotCompiledException();
    }

    public List<T> toTreeList(ExprTree<Func1<T, Collection<T>>> expr) {
        SqlVisitor sqlVisitor = new SqlVisitor(getConfig(), getSqlBuilder().getQueryable());
        ISqlColumnExpression column = sqlVisitor.toColumn(expr.getTree());
        FieldMetaData fieldMetaData = column.getFieldMetaData();
        if (!fieldMetaData.hasNavigate()) {
            throw new SqLinkException("toTreeList指定的字段需要被@Navigate修饰");
        }
        NavigateData navigateData = fieldMetaData.getNavigateData();
        MetaData metaData = MetaDataCache.getMetaData(fieldMetaData.getParentType());
        FieldMetaData parent = metaData.getFieldMetaDataByFieldName(navigateData.getTargetFieldName());
        FieldMetaData child = metaData.getFieldMetaDataByFieldName(navigateData.getSelfFieldName());
        return buildTree(toList(), child, parent, fieldMetaData, expr.getDelegate());
    }

    // endregion

    // region [FAST RETURN]

    /**
     * 检查表中是否存在至少一条数据
     */
    public boolean any() {
        return any0(null);
    }

    /**
     * 检查表中是否存在至少一条数据<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     */
    public boolean any(@Expr(Expr.BodyType.Expr) Func1<T, Boolean> func) {
        throw new NotCompiledException();
    }

    public boolean any(ExprTree<Func1<T, Boolean>> expr) {
        return any0(expr.getTree());
    }

    /**
     * 聚合函数COUNT
     */
    public long count() {
        return count0(null);
    }

    /**
     * 聚合函数COUNT<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     */
    public <R> long count(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R> long count(ExprTree<Func1<T, R>> expr) {
        return count0(expr.getTree());
    }


    /**
     * 聚合函数SUM
     * <p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red)
     */
    public <R extends Number> R sum(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R extends Number> R sum(ExprTree<Func1<T, R>> expr) {
        return sum0(expr.getTree());
    }


    /**
     * 聚合函数AVG
     * <p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red))
     */
    public <R extends Number> BigDecimal avg(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R extends Number> BigDecimal avg(ExprTree<Func1<T, R>> expr) {
        return avg0(expr.getTree());
    }

    /**
     * 聚合函数MAX
     * <p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red)))
     */
    public <R extends Number> R max(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R extends Number> R max(ExprTree<Func1<T, R>> expr) {
        return max0(expr.getTree());
    }

    /**
     * 聚合函数MIN
     * <p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red)))
     */
    public <R extends Number> R min(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R extends Number> R min(ExprTree<Func1<T, R>> expr) {
        return min0(expr.getTree());
    }
    // endregion

    // region [WITH]

//    public LQuery<T> asWith() {
//        SqLinkConfig config = getConfig();
//        SqlExpressionFactory factory = config.getSqlExpressionFactory();
//        ISqlQueryableExpression queryable = getSqlBuilder().getQueryable();
//        Class<?> target = queryable.getMainTableClass();
//        MetaData metaData = MetaDataCache.getMetaData(target);
//        String asName = ExpressionUtil.getFirst(target);
//        ISqlWithExpression with = factory.with(queryable, metaData.getTableName());
//        QuerySqlBuilder querySqlBuilder = new QuerySqlBuilder(config, factory.queryable(factory.from(with, asName)));
//        return new LQuery<>(querySqlBuilder);
//    }

    // endregion

    // region [UNION]

    protected UnionQuery<T> union(ISqlQueryableExpression query, boolean all) {
        return new UnionQuery<>(getConfig(), this.getSqlBuilder().getQueryable(), query, all);
    }

    public UnionQuery<T> union(LQuery<T> query, boolean all) {
        return union(query.getSqlBuilder().getQueryable(), all);
    }

    public UnionQuery<T> union(LQuery<T> query) {
        return union(query.getSqlBuilder().getQueryable(), false);
    }

    public UnionQuery<T> unionAll(LQuery<T> query) {
        return union(query.getSqlBuilder().getQueryable(), true);
    }

    public UnionQuery<T> union(EndQuery<T> query, boolean all) {
        return union(query.getSqlBuilder().getQueryable(), all);
    }

    public UnionQuery<T> union(EndQuery<T> query) {
        return union(query.getSqlBuilder().getQueryable(), false);
    }

    public UnionQuery<T> unionAll(EndQuery<T> query) {
        return union(query.getSqlBuilder().getQueryable(), true);
    }

    // endregion

    // region [CTE]

    public LQuery<T> asTreeCTE(@Expr(Expr.BodyType.Expr) Func1<T, Collection<T>> expr, int level) {
        throw new NotCompiledException();
    }

    public LQuery<T> asTreeCTE(ExprTree<Func1<T, Collection<T>>> expr, int level) {
        ISqlQueryableExpression queryable = getSqlBuilder().getQueryable();
        SqlVisitor sqlVisitor = new SqlVisitor(getConfig(), queryable);
        ISqlColumnExpression column = sqlVisitor.toColumn(expr.getTree());
        FieldMetaData fieldMetaData = column.getFieldMetaData();
        if (!fieldMetaData.hasNavigate()) {
            throw new SqLinkException("asTreeCTE指定的字段需要被@Navigate修饰");
        }
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        NavigateData navigateData = fieldMetaData.getNavigateData();
        MetaData metaData = MetaDataCache.getMetaData(fieldMetaData.getParentType());
        String parentId = metaData.getFieldMetaDataByFieldName(navigateData.getTargetFieldName()).getColumn();
        String childId = metaData.getFieldMetaDataByFieldName(navigateData.getSelfFieldName()).getColumn();
        ISqlSelectExpression select = queryable.getSelect().copy(getConfig());
        AsName asName = queryable.getFrom().getAsName();
        ISqlRecursionExpression recursion = factory.recursion(queryable, parentId, childId, level);
        ISqlQueryableExpression newQuery = factory.queryable(select, factory.from(recursion, asName));
        getSqlBuilder().setQueryable(newQuery);
        // TODO INCLUDE
        return this;
    }

    public LQuery<T> asTreeCTE(@Expr(Expr.BodyType.Expr) Func1<T, Collection<T>> expr) {
        throw new NotCompiledException();
    }

    public LQuery<T> asTreeCTE(ExprTree<Func1<T, Collection<T>>> expr) {
        return asTreeCTE(expr, 0);
    }

    // endregion

    // region [ANOTHER]

    public LDelete<T> toDelete() {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        ISqlQueryableExpression queryable = getSqlBuilder().getQueryable();
        MetaData metaData = MetaDataCache.getMetaData(queryable.getMainTableClass());
        FieldMetaData primary = metaData.getPrimary();
        DeleteSqlBuilder deleteSqlBuilder = new DeleteSqlBuilder(getConfig(), queryable.getMainTableClass());
        ISqlQueryableExpression copy = queryable.copy(getConfig());
        // 某些数据库不支持 a.xx in (select b.xx from b), 所以需要在外边包一层 a.xx in (select b.xx from (select * from b))
        ISqlSelectExpression select = factory.select(Collections.singletonList(factory.column(primary, copy.getFrom().getAsName())), copy.getMainTableClass());
        ISqlQueryableExpression warpQuery = factory.queryable(select, factory.from(copy, copy.getFrom().getAsName()));
        warpQuery.setChanged(true);
        deleteSqlBuilder.addWhere(factory.binary(SqlOperator.IN, factory.column(primary, deleteSqlBuilder.getFrom().getAsName()), warpQuery));
        return new LDelete<>(deleteSqlBuilder);
    }

    public LUpdate<T> toUpdate() {
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        ISqlQueryableExpression queryable = getSqlBuilder().getQueryable();
        MetaData metaData = MetaDataCache.getMetaData(queryable.getMainTableClass());
        FieldMetaData primary = metaData.getPrimary();
        ISqlUpdateExpression update = factory.update(queryable.getMainTableClass(), queryable.getFrom().getAsName());
        ISqlQueryableExpression copy = queryable.copy(getConfig());
        ISqlSelectExpression select = factory.select(Collections.singletonList(factory.column(primary, copy.getFrom().getAsName())), copy.getMainTableClass());
        // 某些数据库不支持 a.xx in (select b.xx from b), 所以需要在外边包一层 a.xx in (select b.xx from (select * from b))
        ISqlQueryableExpression warpQuery = factory.queryable(select, factory.from(copy, copy.getFrom().getAsName()));
        warpQuery.setChanged(true);
        update.addWhere(factory.binary(SqlOperator.IN, factory.column(primary, update.getFrom().getAsName()), warpQuery));
        return new LUpdate<>(new UpdateSqlBuilder(getConfig(), update));
    }

    // endregion

    // region [Filter]

    public LQuery<T> DisableFilter(String filterId) {
        ignoreFilterIds.add(filterId);
        return this;
    }

    public LQuery<T> DisableFilterAll() {
        ignoreFilterAll = true;
        return this;
    }

    // endregion
}
