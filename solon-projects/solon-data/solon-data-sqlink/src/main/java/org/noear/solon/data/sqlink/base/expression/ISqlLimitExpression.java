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

/**
 * limit表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlLimitExpression extends ISqlExpression {
    /**
     * 获取偏移量
     */
    long getOffset();

    /**
     * 获取行数
     */
    long getRows();

    /**
     * 设置偏移量
     */
    void setOffset(long offset);

    /**
     * 设置行数
     */
    void setRows(long rows);

    /**
     * 是否只有行数
     */
    default boolean onlyHasRows() {
        return getRows() > 0 && getOffset() <= 0;
    }

    /**
     * 是否有行数和偏移量
     */
    default boolean hasRowsAndOffset() {
        return getRows() > 0 && getOffset() > 0;
    }

    /**
     * 是否有行数或偏移量
     */
    default boolean hasRowsOrOffset() {
        return getRows() > 0 || getOffset() > 0;
    }

    @Override
    default ISqlLimitExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.limit(getOffset(), getRows());
    }
}
