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
package org.noear.solon.data.sqlink.core.api.crud.read;

import io.github.kiryu1223.expressionTree.delegate.Func0;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.core.api.Result;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;

/**
 * 空表查询过程（用于数据库计算）
 *
 * @author kiryu1223
 * @since 3.0
 */
public class EmptyQuery extends QueryBase
{
    public EmptyQuery(IConfig config)
    {
        super(config, Empty.class);
    }

    public <R extends Result> LQuery<R> select(@Expr Func0<R> expr)
    {
        throw new NotCompiledException();
    }

    public <R extends Result> LQuery<R> select(ExprTree<Func0<R>> expr)
    {
        boolean single = select(expr.getTree());
        singleCheck(single);
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func0<R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func0<R>> expr)
    {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }
}
