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
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.expression.AsName;
import org.noear.solon.data.sqlink.base.expression.ISqlColumnExpression;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlColumnExpression implements ISqlColumnExpression {
    private final FieldMetaData fieldMetaData;
    private AsName tableAsName;

    public SqlColumnExpression(FieldMetaData fieldMetaData, AsName tableAsName) {
        this.fieldMetaData = fieldMetaData;
        this.tableAsName = tableAsName;
    }

    @Override
    public FieldMetaData getFieldMetaData() {
        return fieldMetaData;
    }

    @Override
    public AsName getTableAsName() {
        return tableAsName;
    }

    @Override
    public void setTableAsName(AsName tableAsName) {
        this.tableAsName = tableAsName;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        SqLinkDialect dialect = config.getDisambiguation();
        String columnName = dialect.disambiguation(getFieldMetaData().getColumn());
        if (tableAsName != null) {
            return dialect.disambiguation(tableAsName.getName()) + "." + columnName;
        }
        else {
            return columnName;
        }
    }
}
