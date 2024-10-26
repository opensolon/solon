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
package org.noear.solon.data.sqlink.base.toBean.Include;



import org.noear.solon.data.sqlink.base.expression.ISqlColumnExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;

import java.util.ArrayList;
import java.util.List;

public class IncludeSet
{
    private final ISqlColumnExpression columnExpression;
    private final ISqlExpression cond;
    private final List<IncludeSet> includeSets = new ArrayList<>();

    public IncludeSet(ISqlColumnExpression columnExpression, ISqlExpression cond)
    {
        this.columnExpression = columnExpression;
        this.cond = cond;
    }

    public IncludeSet(ISqlColumnExpression columnExpression)
    {
        this(columnExpression,null);
    }

    public ISqlColumnExpression getColumnExpression()
    {
        return columnExpression;
    }

    public ISqlExpression getCond()
    {
        return cond;
    }

    public boolean hasCond()
    {
        return cond != null;
    }

    public List<IncludeSet> getIncludeSets()
    {
        return includeSets;
    }

    public IncludeSet getLastIncludeSet()
    {
        return includeSets.get(includeSets.size() - 1);
    }
}
