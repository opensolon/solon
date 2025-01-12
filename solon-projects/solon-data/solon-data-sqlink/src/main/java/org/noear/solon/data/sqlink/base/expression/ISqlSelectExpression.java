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
package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * select表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlSelectExpression extends ISqlExpression {
    /**
     * 获取需要返回的列
     */
    List<ISqlExpression> getColumns();

    /**
     * 是否是单列查询
     */
    boolean isSingle();

    /**
     * 获取目标类型
     */
    Class<?> getTarget();

    /**
     * 是否是去重查询
     */
    boolean isDistinct();

    /**
     * 设置需要返回的列
     */
    void setColumns(List<ISqlExpression> columns);

    /**
     * 增加需要返回的列
     */
    void addColumn(ISqlExpression column);

    /**
     * 设置是否是单列查询
     */
    void setSingle(boolean single);

    /**
     * 设置是否是去重查询
     */
    void setDistinct(boolean distinct);

    /**
     * 设置目标类型
     */
    void setTarget(Class<?> target);

    @Override
    default ISqlSelectExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<ISqlExpression> newColumns = new ArrayList<>(getColumns().size());
        for (ISqlExpression column : getColumns()) {
            newColumns.add(column.copy(config));
        }
        return factory.select(newColumns, getTarget(), isSingle(), isDistinct());
    }
}
