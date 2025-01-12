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

import java.util.List;

/**
 * 条件表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlConditionsExpression extends ISqlExpression {
    /**
     * 获取条件表达式集合
     */
    List<ISqlExpression> getConditions();

    /**
     * 添加条件表达式
     *
     * @param cond 条件表达式
     */
    default void addCondition(ISqlExpression cond) {
        getConditions().add(cond);
    }

    /**
     * 判断是否为空
     */
    default boolean isEmpty() {
        return getConditions().isEmpty();
    }

    @Override
    default ISqlConditionsExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlConditionsExpression newConditions = factory.condition();
        for (ISqlExpression condition : getConditions()) {
            newConditions.getConditions().add(condition.copy(config));
        }
        return newConditions;
    }
}
