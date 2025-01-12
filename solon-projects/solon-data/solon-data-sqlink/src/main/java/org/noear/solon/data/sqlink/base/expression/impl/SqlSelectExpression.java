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
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSelectExpression;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlSelectExpression implements ISqlSelectExpression {
    protected List<ISqlExpression> columns;
    protected boolean distinct;
    protected Class<?> target;
    protected boolean isSingle;

    SqlSelectExpression(List<ISqlExpression> columns, Class<?> target, boolean isSingle, boolean isDistinct) {
        this.columns = columns;
        this.target = target;
        this.isSingle = isSingle;
        this.distinct = isDistinct;
    }

    @Override
    public List<ISqlExpression> getColumns() {
        return columns;
    }

    public void setColumns(List<ISqlExpression> columns) {
        this.columns = columns;
    }

    @Override
    public void addColumn(ISqlExpression column) {
        columns.add(column);
    }

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        List<String> strings = new ArrayList<>(getColumns().size());
        for (ISqlExpression sqlExpression : getColumns()) {
            strings.add(sqlExpression.getSqlAndValue(config, values));
        }
        String col = String.join(",", strings);
        List<String> result = new ArrayList<>();
        result.add("SELECT");
        if (isDistinct()) {
            result.add("DISTINCT");
        }
        result.add(col);
        return String.join(" ", result);
    }
}
