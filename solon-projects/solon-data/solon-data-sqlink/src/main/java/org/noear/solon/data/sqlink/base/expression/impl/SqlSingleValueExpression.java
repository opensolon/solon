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

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.metaData.IConverter;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;
import org.noear.solon.data.sqlink.base.sqlExt.ISqlKeywords;

import java.util.List;

import static com.sun.jmx.mbeanserver.Util.cast;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlSingleValueExpression extends SqlValueExpression implements ISqlSingleValueExpression
{
    private final Object value;

    SqlSingleValueExpression(Object value)
    {
        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (getValue() == null)
        {
            return "NULL";
        }
        else if (getValue() instanceof ISqlKeywords)
        {
            ISqlKeywords keywords = (ISqlKeywords) getValue();
            return keywords.getKeyword(config);
        }
        else
        {
            if (values != null) values.add(getValue());
            return "?";
        }
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values, IConverter<?, ?> converter, PropertyMetaData propertyMetaData)
    {
        if (getValue() == null)
        {
            return "NULL";
        }
        else
        {
            if (values != null) values.add(converter.toDb(cast(getValue()), propertyMetaData));
            return "?";
        }
    }
}
