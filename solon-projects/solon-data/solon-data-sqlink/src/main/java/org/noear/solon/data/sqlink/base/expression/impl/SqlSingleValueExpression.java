/*
 * Copyright 2017-2025 noear.org and authors
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
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.base.sqlExt.ISqlKeywords;

import java.util.List;


/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlSingleValueExpression extends SqlValueExpression implements ISqlSingleValueExpression {
    private final Object value;

    public SqlSingleValueExpression(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        if (getValue() == null) {
            return "NULL";
        }
        // 如果是关键字，则直接返回字符串拼接到SQL
        else if (getValue() instanceof ISqlKeywords) {
            ISqlKeywords keywords = (ISqlKeywords) getValue();
            return keywords.getKeyword(config);
        }
        else {
            if (values != null) values.add(new SqlValue(getValue()));
            return "?";
        }
    }

//    @Override
//    public String getSqlAndValue(IConfig config, List<Object> values, IConverter<?, ?> converter, FieldMetaData propertyMetaData)
//    {
//        if (getValue() == null)
//        {
//            return "NULL";
//        }
//        else
//        {
//            if (values != null) values.add(converter.toDb(cast(getValue()), propertyMetaData));
//            return "?";
//        }
//    }
}
