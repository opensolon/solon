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

import io.github.kiryu1223.expressionTree.expressions.*;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;

import java.util.LinkedHashMap;

/**
 * 分组表达式解析器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class GroupByVisitor extends SqlVisitor {
    public GroupByVisitor(SqLinkConfig config) {
        super(config);
    }

    public GroupByVisitor(SqLinkConfig config, int offset) {
        super(config, offset);
    }

    /**
     * 解析分组表达式
     */
    @Override
    public ISqlExpression visit(NewExpression newExpression) {
        BlockExpression classBody = newExpression.getClassBody();
        if (classBody == null) {
            return super.visit(newExpression);
        }
        else {
            LinkedHashMap<String, ISqlExpression> contextMap = new LinkedHashMap<>();
            for (Expression expression : classBody.getExpressions()) {
                if (expression.getKind() == Kind.Variable) {
                    VariableExpression variableExpression = (VariableExpression) expression;
                    String name = variableExpression.getName();
                    ISqlExpression sqlExpression = visit(variableExpression.getInit());
                    contextMap.put(name, sqlExpression);
                }
            }
            return factory.groupBy(contextMap);
        }
    }

    @Override
    protected GroupByVisitor getSelf() {
        return new GroupByVisitor(config);
    }

    @Override
    protected SqlVisitor getSelf(int offset) {
        return new GroupByVisitor(config, offset);
    }
}
