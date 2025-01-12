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

import org.noear.solon.data.sqlink.api.crud.read.group.Grouper;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlGroupByExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlGroupByExpression implements ISqlGroupByExpression {
    protected final LinkedHashMap<String, ISqlExpression> columns = new LinkedHashMap<>();
    protected Class<? extends Grouper> grouperType;

    public void setColumns(LinkedHashMap<String, ISqlExpression> columns) {
        this.columns.putAll(columns);
    }

    public LinkedHashMap<String, ISqlExpression> getColumns() {
        return columns;
    }

    @Override
    public Class<? extends Grouper> getGrouperType() {
        return grouperType;
    }

    @Override
    public void setGrouperType(Class<? extends Grouper> grouperType) {
        this.grouperType = grouperType;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        if (!hasColumns()) return "";
        List<String> strings = new ArrayList<>();
        for (ISqlExpression column : getColumns().values()) {
            strings.add(column.getSqlAndValue(config, values));
        }
        return "GROUP BY " + String.join(",", strings);
    }
}
