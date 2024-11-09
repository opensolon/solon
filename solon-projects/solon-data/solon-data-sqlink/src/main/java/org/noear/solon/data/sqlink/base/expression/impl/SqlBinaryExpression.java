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
package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.core.visitor.methods.StringMethods;

import java.util.List;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.isString;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlBinaryExpression implements ISqlBinaryExpression {
    private final SqlOperator operator;
    private final ISqlExpression left;
    private final ISqlExpression right;

    public SqlBinaryExpression(SqlOperator operator, ISqlExpression left, ISqlExpression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public SqlOperator getOperator() {
        return operator;
    }

    @Override
    public ISqlExpression getLeft() {
        return left;
    }

    @Override
    public ISqlExpression getRight() {
        return right;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        SqlOperator operator = getOperator();
        StringBuilder sb = new StringBuilder();
        if (operator == SqlOperator.PLUS
                && (getLeft() instanceof ISqlSingleValueExpression && ((ISqlSingleValueExpression) getLeft()).getValue() != null && isString(((ISqlSingleValueExpression) getLeft()).getType()))
                || (getRight() instanceof ISqlSingleValueExpression && ((ISqlSingleValueExpression) getRight()).getValue() != null && isString(((ISqlSingleValueExpression) getRight()).getType()))
        ) {
            ISqlTemplateExpression concat = StringMethods.concat(config, getLeft(), getRight());
            String sqlAndValue = concat.getSqlAndValue(config, values);
            sb.append(sqlAndValue);
        }
        else {
            sb.append(getLeft().getSqlAndValue(config, values));
            sb.append(" ");
            // (= NULL) => (IS NULL)
            if (operator == SqlOperator.EQ
                    && getRight() instanceof ISqlSingleValueExpression
                    && ((ISqlSingleValueExpression) getRight()).getValue() == null) {
                sb.append(SqlOperator.IS.getOperator());
            }
            else {
                sb.append(operator.getOperator());
            }
            sb.append(" ");
            if (operator == SqlOperator.IN) {
                sb.append("(");
                sb.append(getRight().getSqlAndValue(config, values));
                sb.append(")");
            }
            else {
                sb.append(getRight().getSqlAndValue(config, values));
            }
        }
        return sb.toString();
    }
}
