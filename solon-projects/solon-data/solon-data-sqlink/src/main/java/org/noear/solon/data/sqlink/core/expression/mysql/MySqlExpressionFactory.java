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
package org.noear.solon.data.sqlink.core.expression.mysql;

import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlRecursionExpression;
import org.noear.solon.data.sqlink.base.expression.impl.DefaultSqlExpressionFactory;

/**
 * MySql表达式工厂
 *
 * @author kiryu1223
 * @since 3.0
 */
public class MySqlExpressionFactory extends DefaultSqlExpressionFactory {

    @Override
    public ISqlRecursionExpression recursion(ISqlQueryableExpression queryable, String parentId, String childId, int level) {
        return new MySqlRecursionExpression(queryable, parentId, childId, level);
    }
}
