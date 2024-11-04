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
package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;

/**
 * from表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlFromExpression extends ISqlExpression {
    ISqlTableExpression getSqlTableExpression();

    default boolean isEmptyTable() {
        Class<?> tableClass = getSqlTableExpression().getTableClass();
        MetaData metaData = MetaDataCache.getMetaData(tableClass);
        return metaData.isEmptyTable();
    }

    int getIndex();

    @Override
    default ISqlFromExpression copy(IConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.from(getSqlTableExpression().copy(config), getIndex());
    }
}
