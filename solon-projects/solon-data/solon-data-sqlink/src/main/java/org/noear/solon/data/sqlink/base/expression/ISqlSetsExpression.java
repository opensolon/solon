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

import java.util.Collection;
import java.util.List;

/**
 * 承载多个set表达式的对象
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlSetsExpression extends ISqlExpression {
    /**
     * 获取所有的set表达式
     */
    List<ISqlSetExpression> getSets();

    default boolean isEmpty()
    {
        return getSets().isEmpty();
    }

    /**
     * 添加一个set表达式
     */
    void addSet(ISqlSetExpression sqlSetExpression);

    /**
     * 添加一组set表达式
     */
    void addSet(Collection<ISqlSetExpression> set);

    @Override
    default ISqlSetsExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlSetsExpression newSets = factory.sets();
        for (ISqlSetExpression set : getSets()) {
            newSets.addSet(set.copy(config));
        }
        return newSets;
    }
}
