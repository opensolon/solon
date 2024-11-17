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
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeSet;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;

import java.util.Collection;

/**
 * 抓取过程对象
 *
 * @author kiryu1223
 * @since 3.0
 */
public class IncludeQuery<T, TPreviousProperty> extends LQuery<T> {
    private final IncludeSet curIncludeSet;

    public IncludeQuery(QuerySqlBuilder sqlBuilder) {
        this(sqlBuilder, sqlBuilder.getLastIncludeSet());
    }

    public IncludeQuery(QuerySqlBuilder sqlBuilder, IncludeSet includeSet) {
        super(sqlBuilder);
        this.curIncludeSet = includeSet;
    }

    /**
     * 在抓取到的对象内再次选择字段进行抓取,并且设置简单的条件<p>
     * Include((a) -> a.b,(b) -> b.getId() > 0).thenInclude((b) -> b.c,(c) -> c.getId() > 0).thenInclude((c) -> c.a,(a) -> a.getId() > 0)<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回需要抓取的字段的lambda表达式，这个字段需要被Navigate修饰
     * @param cond 简单的过滤条件
     * @return 抓取过程对象
     */
    public <TProperty> IncludeQuery<T, TProperty> thenInclude(@Expr(Expr.BodyType.Expr) Func1<TPreviousProperty, TProperty> expr, @Expr(Expr.BodyType.Expr) Func1<TProperty, Boolean> cond) {
        throw new RuntimeException();
    }

    /**
     * 在抓取到的对象内再次选择字段进行抓取<p>
     * Include((a) -> a.b).thenInclude((b) -> b.c).thenInclude((c) -> c.a)<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回需要抓取的字段的lambda表达式，这个字段需要被Navigate修饰
     * @return 抓取过程对象
     */
    public <TProperty> IncludeQuery<T, TProperty> thenInclude(@Expr(Expr.BodyType.Expr) Func1<TPreviousProperty, TProperty> expr) {
        throw new RuntimeException();
    }

    /**
     * thenInclude的集合版本
     */
    public <TProperty> IncludeQuery<T, TProperty> thenIncludes(@Expr(Expr.BodyType.Expr) Func1<TPreviousProperty, Collection<TProperty>> expr, @Expr(Expr.BodyType.Expr) Func1<TProperty, Boolean> cond) {
        throw new RuntimeException();
    }

    /**
     * thenInclude的集合版本
     */
    public <TProperty> IncludeQuery<T, TProperty> thenIncludes(@Expr(Expr.BodyType.Expr) Func1<TPreviousProperty, Collection<TProperty>> expr) {
        throw new RuntimeException();
    }

    public <TProperty> IncludeQuery<T, TProperty> thenInclude(ExprTree<Func1<TPreviousProperty, TProperty>> expr, ExprTree<Func1<TProperty, Boolean>> cond) {
        include(expr.getTree(), cond.getTree(), curIncludeSet.getIncludeSets());
        return new IncludeQuery<>(getSqlBuilder(), curIncludeSet.getLastIncludeSet());
    }

    public <TProperty> IncludeQuery<T, TProperty> thenInclude(ExprTree<Func1<TPreviousProperty, TProperty>> expr) {
        include(expr.getTree(), null, curIncludeSet.getIncludeSets());
        return new IncludeQuery<>(getSqlBuilder(), curIncludeSet.getLastIncludeSet());
    }

    public <TProperty> IncludeQuery<T, TProperty> thenIncludes(ExprTree<Func1<TPreviousProperty, Collection<TProperty>>> expr, ExprTree<Func1<TProperty, Boolean>> cond) {
        include(expr.getTree(), cond.getTree(), curIncludeSet.getIncludeSets());
        return new IncludeQuery<>(getSqlBuilder(), curIncludeSet.getLastIncludeSet());
    }

    public <TProperty> IncludeQuery<T, TProperty> thenIncludes(ExprTree<Func1<TPreviousProperty, Collection<TProperty>>> expr) {
        include(expr.getTree(), null, curIncludeSet.getIncludeSets());
        return new IncludeQuery<>(getSqlBuilder(), curIncludeSet.getLastIncludeSet());
    }
}
