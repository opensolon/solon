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
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.core.visitor.methods.StringMethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> sqlValues) {
        SqlOperator operator = getOperator();
        StringBuilder sb = new StringBuilder();
        if (getLeft() instanceof ISqlColumnExpression && getRight() instanceof ISqlValueExpression) {
            ISqlColumnExpression sqlColumn = (ISqlColumnExpression) getLeft();
            ISqlValueExpression valueExpression = (ISqlValueExpression) getRight();
            FieldMetaData fieldMetaData = sqlColumn.getFieldMetaData();
            if (operator == SqlOperator.PLUS && fieldMetaData.getType() == String.class && valueExpression instanceof ISqlSingleValueExpression && valueExpression.nouNull() && ((ISqlSingleValueExpression) valueExpression).getValue() instanceof String) {
                ISqlTemplateExpression concat = StringMethods.concat(config, sqlColumn, valueExpression);
                sb.append(concat.getSql(config));
            }
            else {
                sb.append(sqlColumn.getSqlAndValue(config, sqlValues));
                sb.append(" ");
                if (operator == SqlOperator.EQ
                        && !valueExpression.nouNull()) {
                    sb.append(SqlOperator.IS.getOperator());
                }
                else {
                    sb.append(operator.getOperator());
                }
                sb.append(" ");

            }
            if (valueExpression instanceof ISqlSingleValueExpression) {
                ISqlSingleValueExpression singleValue = (ISqlSingleValueExpression) valueExpression;
                sqlValues.add(new SqlValue(singleValue.getValue(), fieldMetaData.getTypeHandler(), fieldMetaData.getOnPut()));
                sb.append("?");
            }
            else {
                ISqlCollectedValueExpression collectedValue = (ISqlCollectedValueExpression) valueExpression;
                // left IN (?,?,?)
                if (operator == SqlOperator.IN) {
                    Collection<?> collection = collectedValue.getCollection();
                    sb.append("(");
                    List<String> strings = new ArrayList<>(collection.size());
                    for (Object o : collection) {
                        sqlValues.add(new SqlValue(o, fieldMetaData.getTypeHandler(), fieldMetaData.getOnPut()));
                        strings.add("?");
                    }
                    sb.append(String.join(collectedValue.getDelimiter(), strings));
                    sb.append(")");
                }
                else {
                    sqlValues.add(new SqlValue(collectedValue.getCollection(), fieldMetaData.getTypeHandler(), fieldMetaData.getOnPut()));
                    sb.append("?");
                }
            }
        }
        else if (getRight() instanceof ISqlColumnExpression && getLeft() instanceof ISqlValueExpression) {
            ISqlColumnExpression sqlColumn = (ISqlColumnExpression) getRight();
            ISqlValueExpression valueExpression = (ISqlValueExpression) getLeft();
            FieldMetaData fieldMetaData = sqlColumn.getFieldMetaData();
            if (valueExpression instanceof ISqlSingleValueExpression) {
                ISqlSingleValueExpression singleValue = (ISqlSingleValueExpression) valueExpression;
                sqlValues.add(new SqlValue(singleValue.getValue(), fieldMetaData.getTypeHandler(), fieldMetaData.getOnPut()));
            }
            else {
                ISqlCollectedValueExpression collectedValue = (ISqlCollectedValueExpression) valueExpression;
                sqlValues.add(new SqlValue(collectedValue.getCollection(), fieldMetaData.getTypeHandler(), fieldMetaData.getOnPut()));
            }
            if (operator == SqlOperator.PLUS && fieldMetaData.getType() == String.class && valueExpression instanceof ISqlSingleValueExpression && valueExpression.nouNull() && ((ISqlSingleValueExpression) valueExpression).getValue() instanceof String) {
                ISqlTemplateExpression concat = StringMethods.concat(config, valueExpression, sqlColumn);
                sb.append(concat.getSql(config));
            }
            else {
                sb.append("?");
                sb.append(" ");
                if (operator == SqlOperator.EQ
                        && !valueExpression.nouNull()) {
                    sb.append(SqlOperator.IS.getOperator());
                }
                else {
                    sb.append(operator.getOperator());
                }
                sb.append(" ");
                sb.append(sqlColumn.getSqlAndValue(config, sqlValues));
            }
        }
        else {
            if (operator == SqlOperator.PLUS
                    && ((getLeft() instanceof ISqlSingleValueExpression && ((ISqlSingleValueExpression) getLeft()).getValue() instanceof String)
                    &&
                    (getRight() instanceof ISqlSingleValueExpression && ((ISqlSingleValueExpression) getRight()).getValue() instanceof String))) {
                ISqlTemplateExpression concat = StringMethods.concat(config, getLeft(), getRight());
                sb.append(concat.getSql(config));
            }
            else {
                sb.append(getLeft().getSqlAndValue(config, sqlValues));
                sb.append(" ");
                // (= NULL) => (IS NULL)
                if (operator == SqlOperator.EQ
                        && getRight() instanceof ISqlSingleValueExpression
                        && !((ISqlSingleValueExpression) getRight()).nouNull()) {
                    sb.append(SqlOperator.IS.getOperator());
                }
                else {
                    sb.append(operator.getOperator());
                }
                sb.append(" ");
                if (operator == SqlOperator.IN) {
                    sb.append("(");
                    sb.append(getRight().getSqlAndValue(config, sqlValues));
                    sb.append(")");
                }
                else {
                    sb.append(getRight().getSqlAndValue(config, sqlValues));
                }
            }
        }
        return sb.toString();
    }
}
