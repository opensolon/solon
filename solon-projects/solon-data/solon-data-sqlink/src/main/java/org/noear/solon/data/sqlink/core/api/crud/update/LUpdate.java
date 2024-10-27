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
package org.noear.solon.data.sqlink.core.api.crud.update;


import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Action1;
import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.delegate.Func2;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LUpdate<T> extends UpdateBase
{
    // region [INIT]
    public LUpdate(IConfig config, Class<T> t)
    {
        super(config, t);
    }
    // endregion

    //region [JOIN]
    public <Tn> LUpdate2<T, Tn> innerJoin(Class<Tn> target, @Expr Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate2<T, Tn> innerJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LUpdate2<>(getSqlBuilder());
    }

    public <Tn> LUpdate2<T, Tn> leftJoin(Class<Tn> target, @Expr Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate2<T, Tn> leftJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LUpdate2<>(getSqlBuilder());
    }

    public <Tn> LUpdate2<T, Tn> rightJoin(Class<Tn> target, @Expr Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate2<T, Tn> rightJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LUpdate2<>(getSqlBuilder());
    }
    //endregion

    //region [SET]
    public LUpdate<T> set(@Expr Action1<T> action)
    {
        throw new NotCompiledException();
    }

    public LUpdate<T> set(ExprTree<Action1<T>> expr)
    {
        set(expr.getTree());
        return this;
    }
    //endregion

    //region [WHERE]

    public LUpdate<T> where(@Expr Func1<T, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LUpdate<T> where(ExprTree<Func1<T, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    //endregion
}
