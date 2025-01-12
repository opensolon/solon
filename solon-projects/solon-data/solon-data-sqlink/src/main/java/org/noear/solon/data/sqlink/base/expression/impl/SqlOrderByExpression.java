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
import org.noear.solon.data.sqlink.base.expression.ISqlOrderByExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlOrderExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlOrderByExpression implements ISqlOrderByExpression {
    protected final List<ISqlOrderExpression> sqlOrders = new ArrayList<>();

    @Override
    public void addOrder(ISqlOrderExpression sqlOrder) {
        sqlOrders.add(sqlOrder);
    }

    @Override
    public List<ISqlOrderExpression> getSqlOrders() {
        return sqlOrders;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        if (isEmpty()) return "";
        List<String> strings = new ArrayList<>(getSqlOrders().size());
        for (ISqlOrderExpression sqlOrder : getSqlOrders()) {
            strings.add(sqlOrder.getSqlAndValue(config, values));
        }
        return "ORDER BY " + String.join(",", strings);
    }
}
