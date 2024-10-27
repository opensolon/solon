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
package org.noear.solon.data.sqlink.core.api.crud.read.group;

import org.noear.solon.data.sqlink.core.exception.SqlFunctionInvokeException;
import io.github.kiryu1223.expressionTree.delegate.Func5;

import java.math.BigDecimal;

import static org.noear.solon.data.sqlink.core.exception.Winner.boom;

public abstract class SqlAggregation5<T1, T2, T3, T4, T5> implements IAggregation
{
    public <R> long count(Func5<T1, T2, T3, T4, T5, R> expr)
    {
        boom();
        return 0;
    }

    public <R> BigDecimal sum(Func5<T1, T2, T3, T4, T5, R> expr)
    {
        boom();
        return BigDecimal.ZERO;
    }

    public <R> BigDecimal avg(Func5<T1, T2, T3, T4, T5, R> expr)
    {
        boom();
        return BigDecimal.ZERO;
    }

    public <R> R max(Func5<T1, T2, T3, T4, T5, R> expr)
    {
        boom();
        return (R) new Object();
    }

    public <R> R min(Func5<T1, T2, T3, T4, T5, R> expr)
    {
        boom();
        return (R) new Object();
    }
}
