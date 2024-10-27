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

import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Action10;
import io.github.kiryu1223.expressionTree.delegate.Func10;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends UpdateBase
{
    public LUpdate10(UpdateSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [SET]
    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> set(@Expr Action10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> action)
    {
        throw new NotCompiledException();
    }

    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> set(ExprTree<Action10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> expr)
    {
        set(expr.getTree());
        return this;
    }
    //endregion

    //region [WHERE]
    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> where(@Expr Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> where(ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    //endregion
}
