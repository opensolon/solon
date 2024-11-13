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
package org.noear.solon.data.sqlink.core.visitor;

import io.github.kiryu1223.expressionTree.expressions.FieldSelectExpression;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlGroupByExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;

import java.util.Map;

/**
 * having表达式解析器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class HavingVisitor extends SqlVisitor {
    private final ISqlQueryableExpression queryable;

    public HavingVisitor(SqLinkConfig config, ISqlQueryableExpression queryable) {
        super(config);
        this.queryable = queryable;
    }

    public HavingVisitor(SqLinkConfig config, ISqlQueryableExpression queryable, int offset) {
        super(config, offset);
        this.queryable = queryable;
    }


    @Override
    public ISqlExpression visit(FieldSelectExpression fieldSelect) {
        if (ExpressionUtil.isGroupKey(parameters, fieldSelect)) // g.key
        {
            ISqlGroupByExpression groupBy = queryable.getGroupBy();
            return groupBy.getColumns().get("key");
        }
        else if (ExpressionUtil.isGroupKey(parameters, fieldSelect.getExpr())) // g.key.xxx
        {
            Map<String, ISqlExpression> columns = queryable.getGroupBy().getColumns();
            return columns.get(fieldSelect.getField().getName());
        }
        else {
            return super.visit(fieldSelect);
        }
    }

    @Override
    protected SqlVisitor getSelf() {
        return new HavingVisitor(config, queryable);
    }

    @Override
    protected SqlVisitor getSelf(int offset) {
        return new HavingVisitor(config, queryable, offset);
    }
}
