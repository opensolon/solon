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
package org.noear.solon.data.sqlink.api.crud.read.group;

import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import org.noear.solon.data.sqlink.api.Result;
import org.noear.solon.data.sqlink.api.crud.read.EndQuery;
import org.noear.solon.data.sqlink.api.crud.read.LQuery;
import org.noear.solon.data.sqlink.api.crud.read.QueryBase;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import org.noear.solon.data.sqlink.core.page.DefaultPager;
import org.noear.solon.data.sqlink.core.page.PagedResult;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 分组查询过程对象
 *
 * @author kiryu1223
 * @since 3.0
 */
public class GroupedQuery<Key, T> extends QueryBase {
    public GroupedQuery(QuerySqlBuilder sqlBuilder) {
        super(sqlBuilder);
    }

    // region [HAVING]

    /**
     * 设置having<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public GroupedQuery<Key, T> having(@Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, Boolean> func) {
        throw new NotCompiledException();
    }

    public GroupedQuery<Key, T> having(ExprTree<Func1<Group<Key, T>, Boolean>> expr) {
        having(expr.getTree());
        return this;
    }

    public GroupedQuery<Key, T> havingIf(boolean condition, @Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, Boolean> func) {
        throw new NotCompiledException();
    }

    public GroupedQuery<Key, T> havingIf(boolean condition, ExprTree<Func1<Group<Key, T>, Boolean>> expr) {
        if (condition) having(expr.getTree());
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
    public <R> GroupedQuery<Key, T> orderBy(@Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, R> expr, boolean asc) {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery<Key, T> orderBy(ExprTree<Func1<Group<Key, T>, R>> expr, boolean asc) {
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
    public <R> GroupedQuery<Key, T> orderBy(@Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, R> expr) {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery<Key, T> orderBy(ExprTree<Func1<Group<Key, T>, R>> expr) {
        orderBy(expr, true);
        return this;
    }

    public <R> GroupedQuery<Key, T> orderByDesc(@Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, R> expr) {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery<Key, T> orderByDesc(ExprTree<Func1<Group<Key, T>, R>> expr) {
        orderBy(expr, false);
        return this;
    }

    public <R> GroupedQuery<Key, T> orderByIf(boolean condition, @Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, R> expr, boolean asc) {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery<Key, T> orderByIf(boolean condition, ExprTree<Func1<Group<Key, T>, R>> expr, boolean asc) {
        if (condition) orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> GroupedQuery<Key, T> orderByIf(boolean condition, @Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, R> expr) {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery<Key, T> orderByIf(boolean condition, ExprTree<Func1<Group<Key, T>, R>> expr) {
        if (condition) orderBy(expr, true);
        return this;
    }

    public <R> GroupedQuery<Key, T> orderByDescIf(boolean condition, @Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, R> expr) {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery<Key, T> orderByDescIf(boolean condition, ExprTree<Func1<Group<Key, T>, R>> expr) {
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
    public GroupedQuery<Key, T> limit(long rows) {
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
    public GroupedQuery<Key, T> limit(long offset, long rows) {
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
    public GroupedQuery<Key, T> limitIf(boolean condition, long rows) {
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
    public GroupedQuery<Key, T> limitIf(boolean condition, long offset, long rows) {
        if (condition) limit0(offset, rows);
        return this;
    }

    // endregion

    // region [SELECT]

    /**
     * 设置select<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回一个继承于Result的匿名对象的lambda表达式((a) -> new Result(){...})，初始化段{...}内编写需要select的字段(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <R>  Result
     * @return 基于Result类型的新查询过程对象
     */
    public <R extends Result> LQuery<? extends R> select(@Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, R> expr) {
        throw new NotCompiledException();
    }

    public <R extends Result> LQuery<? extends R> select(ExprTree<Func1<Group<Key, T>, R>> expr) {
        singleCheck(select(expr.getTree()));
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    /**
     * 此重载用于当想要返回某个字段的情况((r) -> r.getId),因为select泛型限制为必须是Result的子类<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回一个值的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return 终结查询过程
     */
    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func1<Group<Key, T>, R> expr) {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func1<Group<Key, T>, R>> expr) {
        select(expr.getTree());
        return new EndQuery<>(boxedQuerySqlBuilder());
    }

    public <R> GroupedQuery<Key, T> includes(@Expr(Expr.BodyType.Expr) Func1<Key, Collection<R>> expr) {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery<Key, T> includes(ExprTree<Func1<Key, Collection<R>>> expr) {
        include(expr.getTree());
        return this;
    }

    // endregion

    // region [toAny]

    /**
     * list集合形式返回数据，无数据则返回空list
     *
     * @return List
     */
    @Override
    public List<Key> toList() {
        return super.toList();
    }

    /**
     * 分页返回数据，无数据则返回空List
     *
     * @param pageIndex 页编号 默认1开始
     * @param pageSize  页长度 默认大于等于1
     * @return 分页数据
     */
    public PagedResult<Key> toPagedResult(long pageIndex, long pageSize) {
        return toPagedResult0(pageIndex, pageSize, DefaultPager.instance);
    }

    /**
     * 分页返回数据，无数据则返回空List
     *
     * @param pageIndex 页编号 默认1开始
     * @param pageSize  页长度 默认大于等于1
     * @return 分页数据
     */
    public PagedResult<Key> toPagedResult(int pageIndex, int pageSize) {
        return toPagedResult((long) pageIndex, (long) pageSize);
    }

    // endregion

    // region [OTHER]

    /**
     * 设置distinct
     *
     * @return this
     */
    public GroupedQuery<Key, T> distinct() {
        distinct0(true);
        return this;
    }

    /**
     * 设置distinct
     *
     * @param condition 是否distinct
     * @return this
     */
    public GroupedQuery<Key, T> distinct(boolean condition) {
        distinct0(condition);
        return this;
    }

    // endregion

    // region [AGGREGATE]

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
    public List<Long> count() {
        return groupByCount0(null);
    }

    /**
     * 聚合函数COUNT<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     */
    public <R> List<Long> count(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R> List<Long> count(ExprTree<Func1<T, R>> expr) {
        return groupByCount0(expr.getTree());
    }


    /**
     * 聚合函数SUM
     * <p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red)
     */
    public <R extends Number> List<R> sum(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R extends Number> List<R> sum(ExprTree<Func1<T, R>> expr) {
        return groupBySum0(expr.getTree());
    }


    /**
     * 聚合函数AVG
     * <p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red))
     */
    public <R extends Number> List<BigDecimal> avg(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R extends Number> List<BigDecimal> avg(ExprTree<Func1<T, R>> expr) {
        return groupByAvg0(expr.getTree());
    }

    /**
     * 聚合函数MAX
     * <p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red)))
     */
    public <R extends Number> List<R> max(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R extends Number> List<R> max(ExprTree<Func1<T, R>> expr) {
        return groupByMax0(expr.getTree());
    }

    /**
     * 聚合函数MIN
     * <p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回需要统计的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red)))
     */
    public <R extends Number> List<R> min(@Expr(Expr.BodyType.Expr) Func1<T, R> func) {
        throw new NotCompiledException();
    }

    public <R extends Number> List<R> min(ExprTree<Func1<T, R>> expr) {
        return groupByMin0(expr.getTree());
    }
    // endregion
}
