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
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.core.exception.SqLinkException;

import java.util.List;

/**
 * 承载java类型，一般不调用他的getSql函数
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlTypeExpression extends ISqlExpression {
    /**
     * 获取java类型
     */
    Class<?> getType();

    @Override
    default String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        throw new SqLinkException("ISqlTypeExpression.getSqlAndValue should not be called");
    }

    @Override
    default ISqlTypeExpression copy(SqLinkConfig config) {
        return config.getSqlExpressionFactory().type(getType());
    }
}
