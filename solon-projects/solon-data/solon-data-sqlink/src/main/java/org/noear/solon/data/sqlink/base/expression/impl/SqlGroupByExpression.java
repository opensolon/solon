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
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlGroupByExpression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlGroupByExpression implements ISqlGroupByExpression {
    protected final LinkedHashMap<String, ISqlExpression> columns = new LinkedHashMap<>();

    public void setColumns(LinkedHashMap<String, ISqlExpression> columns) {
        this.columns.putAll(columns);
    }

    public LinkedHashMap<String, ISqlExpression> getColumns() {
        return columns;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<Object> values) {
        if (getColumns().isEmpty()) return "";
        List<String> strings = new ArrayList<>();
        for (ISqlExpression column : getColumns().values()) {
            strings.add(column.getSqlAndValue(config, values));
        }
        return "GROUP BY " + String.join(",", strings);
    }
}
