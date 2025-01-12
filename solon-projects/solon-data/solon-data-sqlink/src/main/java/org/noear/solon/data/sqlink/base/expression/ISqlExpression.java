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

import java.util.List;

/**
 * sql表达式基类
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlExpression {
    /**
     * 获取sql和参数
     */
    String getSqlAndValue(SqLinkConfig config, List<SqlValue> values);

    /**
     * 获取sql
     */
    default String getSql(SqLinkConfig config) {
        return getSqlAndValue(config, null);
    }

    /**
     * 获取自己的拷贝
     */
    <T extends ISqlExpression> T copy(SqLinkConfig config);
}
