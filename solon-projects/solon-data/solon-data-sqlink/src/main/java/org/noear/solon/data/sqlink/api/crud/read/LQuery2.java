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
package org.noear.solon.data.sqlink.api.crud.read;

import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.delegate.Func2;
import io.github.kiryu1223.expressionTree.delegate.Func3;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;
import org.noear.solon.data.sqlink.api.Result;
import org.noear.solon.data.sqlink.api.crud.read.group.GroupedQuery2;
import org.noear.solon.data.sqlink.api.crud.read.group.Grouper;
import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;

/**
 * 查询过程对象
 *
 * @author kiryu1223
 * @since 3.0
 */
public class LQuery2<T1, T2> extends QueryBase {
    // region [INIT]

    public LQuery2(QuerySqlBuilder sqlBuilder) {
        super(sqlBuilder);
    }

    // endregion

    //region [JOIN]

    protected <Tn> LQuery3<T1, T2, Tn> joinNewQuery() {
        return new LQuery3<>(getSqlBuilder());
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
    public <Tn> LQuery3<T1, T2, Tn> innerJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func3<T1, T2, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery3<T1, T2, Tn> innerJoin(Class<Tn> target, ExprTree<Func3<T1, T2, Tn, Boolean>> expr) {
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
    public <Tn> LQuery3<T1, T2, Tn> innerJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func3<T1, T2, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery3<T1, T2, Tn> innerJoin(LQuery<Tn> target, ExprTree<Func3<T1, T2, Tn, Boolean>> expr) {
        join(JoinType.INNER, target, expr.getTree());
        return joinNewQuery();
    }

    public <Tn> LQuery3<T1, T2, Tn> innerJoinWith(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func3<T1, T2, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery3<T1, T2, Tn> innerJoinWith(LQuery<Tn> target, ExprTree<Func3<T1, T2, Tn, Boolean>> expr) {
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
    public <Tn> LQuery3<T1, T2, Tn> leftJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func3<T1, T2, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery3<T1, T2, Tn> leftJoin(Class<Tn> target, ExprTree<Func3<T1, T2, Tn, Boolean>> expr) {
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
    public <Tn> LQuery3<T1, T2, Tn> leftJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func3<T1, T2, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery3<T1, T2, Tn> leftJoin(LQuery<Tn> target, ExprTree<Func3<T1, T2, Tn, Boolean>> expr) {
        join(JoinType.LEFT, target, expr.getTree());
        return joinNewQuery();
    }

    public <Tn> LQuery3<T1, T2, Tn> leftJoinWith(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func3<T1, T2, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery3<T1, T2, Tn> leftJoinWith(LQuery<Tn> target, ExprTree<Func3<T1, T2, Tn, Boolean>> expr) {
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
    public <Tn> LQuery3<T1, T2, Tn> rightJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func3<T1, T2, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery3<T1, T2, Tn> rightJoin(Class<Tn> target, ExprTree<Func3<T1, T2, Tn, Boolean>> expr) {
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
    public <Tn> LQuery3<T1, T2, Tn> rightJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func3<T1, T2, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery3<T1, T2, Tn> rightJoin(LQuery<Tn> target, ExprTree<Func3<T1, T2, Tn, Boolean>> expr) {
        join(JoinType.RIGHT, target, expr.getTree());
        return joinNewQuery();
    }

    public <Tn> LQuery3<T1, T2, Tn> rightJoinWith(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func3<T1, T2, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LQuery3<T1, T2, Tn> rightJoinWith(LQuery<Tn> target, ExprTree<Func3<T1, T2, Tn, Boolean>> expr) {
        joinWith(JoinType.RIGHT, target, expr.getTree());
        return joinNewQuery();
    }

    // endregion

    // region [WHERE]

    /**
     * 设置where条件<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public LQuery2<T1, T2> where(@Expr(Expr.BodyType.Expr) Func2<T1, T2, Boolean> func) {
        throw new NotCompiledException();
    }

    public LQuery2<T1, T2> where(ExprTree<Func2<T1, T2, Boolean>> expr) {
        where(expr.getTree());
        return this;
    }

    public LQuery2<T1, T2> whereIf(boolean condition, @Expr(Expr.BodyType.Expr) Func2<T1, T2, Boolean> func) {
        throw new NotCompiledException();
    }

    public LQuery2<T1, T2> whereIf(boolean condition, ExprTree<Func2<T1, T2, Boolean>> expr) {
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
    public LQuery2<T1, T2> orWhere(@Expr(Expr.BodyType.Expr) Func2<T1, T2, Boolean> func) {
        throw new NotCompiledException();
    }

    public LQuery2<T1, T2> orWhere(ExprTree<Func2<T1, T2, Boolean>> expr) {
        orWhere(expr.getTree());
        return this;
    }

    public LQuery2<T1, T2> orWhereIf(boolean condition, @Expr(Expr.BodyType.Expr) Func2<T1, T2, Boolean> func) {
        throw new NotCompiledException();
    }

    public LQuery2<T1, T2> orWhereIf(boolean condition, ExprTree<Func2<T1, T2, Boolean>> expr) {
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
    public <R> LQuery2<T1, T2> orderBy(@Expr(Expr.BodyType.Expr) Func2<T1, T2, R> expr, boolean asc) {
        throw new NotCompiledException();
    }

    public <R> LQuery2<T1, T2> orderBy(ExprTree<Func2<T1, T2, R>> expr, boolean asc) {
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
    public <R> LQuery2<T1, T2> orderBy(@Expr(Expr.BodyType.Expr) Func2<T1, T2, R> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery2<T1, T2> orderBy(ExprTree<Func2<T1, T2, R>> expr) {
        orderBy(expr.getTree(), true);
        return this;
    }

    public <R> LQuery2<T1, T2> orderByDesc(@Expr(Expr.BodyType.Expr) Func2<T1, T2, R> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery2<T1, T2> orderByDesc(ExprTree<Func2<T1, T2, R>> expr) {
        orderBy(expr, false);
        return this;
    }
    public <R> LQuery2<T1, T2> orderByIf(boolean condition, @Expr(Expr.BodyType.Expr) Func2<T1, T2, R> expr, boolean asc) {
        throw new NotCompiledException();
    }

    public <R> LQuery2<T1, T2> orderByIf(boolean condition, ExprTree<Func2<T1, T2, R>> expr, boolean asc) {
        if (condition) orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> LQuery2<T1, T2> orderByIf(boolean condition, @Expr(Expr.BodyType.Expr) Func2<T1, T2, R> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery2<T1, T2> orderByIf(boolean condition, ExprTree<Func2<T1, T2, R>> expr) {
        if (condition) orderBy(expr, true);
        return this;
    }

    public <R> LQuery2<T1, T2> orderByDescIf(boolean condition, @Expr(Expr.BodyType.Expr) Func2<T1, T2, R> expr) {
        throw new NotCompiledException();
    }

    public <R> LQuery2<T1, T2> orderByDescIf(boolean condition, ExprTree<Func2<T1, T2, R>> expr) {
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
    public LQuery2<T1, T2> limit(long rows) {
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
    public LQuery2<T1, T2> limit(long offset, long rows) {
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
    public LQuery2<T1, T2> limitIf(boolean condition, long rows) {
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
    public LQuery2<T1, T2> limitIf(boolean condition, long offset, long rows) {
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
    public <Key extends Grouper> GroupedQuery2<? extends Key, T1, T2> groupBy(@Expr Func2<T1, T2, Key> expr) {
        throw new NotCompiledException();
    }

    public <Key extends Grouper> GroupedQuery2<? extends Key, T1, T2> groupBy(ExprTree<Func2<T1, T2, Key>> expr) {
        groupBy(expr.getTree());
        return new GroupedQuery2<>(getSqlBuilder());
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
    public <R extends Result> LQuery<? extends R> select(@Expr(Expr.BodyType.Expr) Func2<T1, T2, R> expr) {
        throw new NotCompiledException();
    }

    public <R extends Result> LQuery<? extends R> select(ExprTree<Func2<T1, T2, R>> expr) {
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
    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func2<T1, T2, R> expr) {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func2<T1, T2, R>> expr) {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }
    // endregion

    //region [OTHER]

    /**
     * 设置distinct
     *
     * @return this
     */
    public LQuery2<T1, T2> distinct() {
        distinct0(true);
        return this;
    }

    /**
     * 设置distinct
     *
     * @param condition 是否distinct
     * @return this
     */
    public LQuery2<T1, T2> distinct(boolean condition) {
        distinct0(condition);
        return this;
    }

    //endregion
}
