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
import org.noear.solon.data.sqlink.base.expression.ISqlBinaryExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlUnaryExpression;
import org.noear.solon.data.sqlink.base.expression.SqlOperator;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlUnaryExpression implements ISqlUnaryExpression {
    private final SqlOperator operator;
    private final ISqlExpression expression;

    public SqlUnaryExpression(SqlOperator operator, ISqlExpression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public SqlOperator getOperator() {
        return operator;
    }

    @Override
    public ISqlExpression getExpression() {
        return expression;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        SqlOperator operator = getOperator();
        String temp = expression.getSqlAndValue(config, values);
        String res;
        if (operator.isLeft()) {
            if (operator == SqlOperator.EXISTS) {
                res = operator.getOperator() + " (" + temp + ")";
            }
            // 优化 NOT_NOT ...
            else if (operator == SqlOperator.NOT && expression instanceof ISqlUnaryExpression
                    && ((ISqlUnaryExpression) expression).getOperator() == SqlOperator.NOT) {
                res = temp;
            }
            else {
                if (expression instanceof ISqlBinaryExpression) {
                    res = operator.getOperator() + " (" + temp + ")";
                }
                else {
                    res = operator.getOperator() + " " + temp;
                }
            }
        }
        else {
            res = temp + " " + operator.getOperator();
        }
        return res;
    }
}
