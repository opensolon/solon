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
package org.noear.solon.core.util;

import org.noear.solon.expression.context.EnhanceContext;

import java.util.Properties;

/**
 * 属性表达式上下文
 *
 * @author noear
 * @since 3.6
 */
public class PropsExpressionContext extends EnhanceContext<Properties, PropsExpressionContext> {
    private Properties main;
    private String referenceKey;

    public PropsExpressionContext(Properties target) {
        super(target);
        forAllowReturnNull(true);
        forAllowPropertyNesting(true);
        forAllowTextAsProperty(false);
        forTypeGuidance(null); //禁用 T(className) 表达式
    }

    public PropsExpressionContext forMain(Properties main) {
        this.main = main;
        return this;
    }

    public PropsExpressionContext forReferenceKey(String referenceKey) {
        this.referenceKey = referenceKey;
        return this;
    }

    @Override
    public Object apply(String name) {
        if (name.indexOf('.') == 0 && referenceKey != null) {
            //本级引用
            int refIdx = referenceKey.lastIndexOf('.');
            if (refIdx > 0) {
                name = referenceKey.substring(0, refIdx) + name;
            }
        }

        String val = null;

        if (target != null) {
            //从"目标属性"获取
            val = target.getProperty(name);
        }

        if (val == null) {
            //从"主属性"获取
            if (main != null) {
                val = main.getProperty(name);
            }

            if (val == null) {
                //从"环镜变量"获取
                val = System.getenv(name);
            }
        }

        return val;
    }
}