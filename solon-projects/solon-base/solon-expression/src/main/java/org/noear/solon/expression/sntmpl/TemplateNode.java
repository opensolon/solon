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
package org.noear.solon.expression.sntmpl;

import org.noear.solon.expression.Expression;
import org.noear.solon.expression.snel.SnEL;

import java.util.List;
import java.util.function.Function;

/**
 * @author noear
 * @since 3.1
 */
public class TemplateNode implements Expression<String> {
    private List<TemplateFragment> fragments;

    public TemplateNode(List<TemplateFragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public String eval(Function context) {
        StringBuilder result = new StringBuilder();
        for (TemplateFragment fragment : fragments) {
            if (fragment.isEvaluable()) {
                // 如果是变量片段，从上下文中获取值
                Object value = SnEL.eval(fragment.getContent(), context);
                result.append(value);
            } else {
                // 如果是文本片段，直接追加
                result.append(fragment.getContent());
            }
        }
        return result.toString();
    }
}