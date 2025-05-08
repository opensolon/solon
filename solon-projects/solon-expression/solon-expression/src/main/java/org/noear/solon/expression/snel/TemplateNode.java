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
package org.noear.solon.expression.snel;

import org.noear.solon.expression.Expression;
import org.noear.solon.expression.context.StandardContext;

import java.util.List;
import java.util.Properties;
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
                Object value;
                if (fragment.getMarker() == '$') {
                    value = getProps(fragment.getContent(), context);
                } else {
                    value = SnEL.eval(fragment.getContent(), context);
                }

                result.append(value);
            } else {
                // 如果是文本片段，直接追加
                result.append(fragment.getContent());
            }
        }
        return result.toString();
    }

    private String getProps(String expr, Function context) {
        //属性，可以传入或者
        Object props = context.apply(SnEL.CONTEXT_PROPS_KEY);

        if (props == null) {
            if (context instanceof StandardContext) {
                props = ((StandardContext) context).properties();
            }

            if (props == null) {
                throw new IllegalArgumentException("Missing property '" + SnEL.CONTEXT_PROPS_KEY + "'");
            }
        }

        int colonIdx = expr.lastIndexOf(':');
        String key = (colonIdx < 0) ? expr : expr.substring(0, colonIdx);
        String def = (colonIdx < 0) ? "" : expr.substring(colonIdx + 1);

        String value = null;
        if (props instanceof Properties) {
            value = ((Properties) props).getProperty(key);
        } else if (props instanceof Function) {
            value = String.valueOf(((Function) props).apply(key));
        } else {
            throw new IllegalArgumentException("Unsupported props type: " + props.getClass());
        }

        if (value == null) {
            return def;
        } else {
            return value;
        }
    }
}