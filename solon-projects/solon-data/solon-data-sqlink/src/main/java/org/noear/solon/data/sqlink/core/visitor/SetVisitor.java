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

import io.github.kiryu1223.expressionTree.expressions.BlockExpression;
import io.github.kiryu1223.expressionTree.expressions.Expression;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSetExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSetsExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * set表达式解析器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SetVisitor extends SqlVisitor {
    public SetVisitor(SqLinkConfig config) {
        super(config);
    }

    public SetVisitor(SqLinkConfig config, int offset) {
        super(config, offset);
    }

    @Override
    public ISqlExpression visit(BlockExpression blockExpression) {
        List<ISqlSetExpression> sqlSetExpressions = new ArrayList<>();
        for (Expression expression : blockExpression.getExpressions()) {
            ISqlExpression visit = visit(expression);
            if (visit instanceof ISqlSetExpression) {
                sqlSetExpressions.add((ISqlSetExpression) visit);
            }
        }
        ISqlSetsExpression sets = factory.sets();
        sets.addSet(sqlSetExpressions);
        return sets;
    }

    @Override
    protected SqlVisitor getSelf() {
        return new SetVisitor(config);
    }

    @Override
    protected SqlVisitor getSelf(int offset) {
        return new SetVisitor(config,offset);
    }
}
