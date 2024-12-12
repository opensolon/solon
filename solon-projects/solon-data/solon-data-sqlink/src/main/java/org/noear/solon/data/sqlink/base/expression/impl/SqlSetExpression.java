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
import org.noear.solon.data.sqlink.base.sqlExt.ISqlKeywords;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlSetExpression implements ISqlSetExpression {
    private final ISqlColumnExpression column;
    private final ISqlExpression value;

    SqlSetExpression(ISqlColumnExpression column, ISqlExpression value) {
        this.column = column;
        this.value = value;
    }

    @Override
    public ISqlColumnExpression getColumn() {
        return column;
    }

    @Override
    public ISqlExpression getValue() {
        return value;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        String set = column.getSqlAndValue(config, values) + " = ";
        FieldMetaData fieldMetaData = column.getFieldMetaData();
        ITypeHandler<?> typeHandler = fieldMetaData.getTypeHandler();
        if (value instanceof ISqlValueExpression) {
            if (value instanceof ISqlSingleValueExpression) {
                ISqlSingleValueExpression sqlSingleValueExpression = (ISqlSingleValueExpression) value;
                Object value1 = sqlSingleValueExpression.getValue();
                if (value1 instanceof ISqlKeywords) {
                    ISqlKeywords iSqlKeywords = (ISqlKeywords) value1;
                    return set + iSqlKeywords.getKeyword(config);
                }
                else {
                    if (values != null) {
                        values.add(new SqlValue(value1, typeHandler, fieldMetaData.getOnPut()));
                    }
                    return set + "?";
                }
            }
            else {
                if (values != null) {
                    ISqlCollectedValueExpression sqlCollectedValueExpression = (ISqlCollectedValueExpression) value;
                    values.add(new SqlValue(sqlCollectedValueExpression.getCollection(), typeHandler, fieldMetaData.getOnPut()));
                }
                return set + "?";
            }
        }
        else {
            return set + value.getSqlAndValue(config, values);
        }
    }
}
