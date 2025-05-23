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
import org.noear.solon.data.sqlink.base.expression.ISqlSetExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSetsExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlSetsExpression implements ISqlSetsExpression {
    private final List<ISqlSetExpression> sets = new ArrayList<>();

    public List<ISqlSetExpression> getSets() {
        return sets;
    }

    public void addSet(ISqlSetExpression set) {
        sets.add(set);
    }

    public void addSet(Collection<ISqlSetExpression> set) {
        sets.addAll(set);
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        List<String> strings = new ArrayList<>(getSets().size());
        for (ISqlSetExpression expression : getSets()) {
            strings.add(expression.getSqlAndValue(config, values));
        }
        return "SET " + String.join(",", strings);
    }
}
