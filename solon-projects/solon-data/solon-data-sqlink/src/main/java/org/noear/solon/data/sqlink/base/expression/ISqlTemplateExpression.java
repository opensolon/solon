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

import java.util.ArrayList;
import java.util.List;

/**
 * 模板表达式，一般用于sql函数
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlTemplateExpression extends ISqlExpression {
    /**
     * 获取模板字符串列表
     */
    List<String> getTemplateStrings();

    /**
     * 获取需要填充的值的列表
     */
    List<? extends ISqlExpression> getExpressions();

    @Override
    default ISqlTemplateExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> newFunctions = new ArrayList<>(getTemplateStrings());
        List<? extends ISqlExpression> newExpressions = new ArrayList<>(getExpressions());
        return factory.template(newFunctions, newExpressions);
    }
}
