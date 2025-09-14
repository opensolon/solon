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

import org.noear.solon.expression.guidance.PropertiesGuidance;
import org.noear.solon.expression.guidance.ReturnGuidance;

import java.util.Properties;
import java.util.function.Function;

/**
 * 属性表达式上下文
 *
 * @author noear
 * @since 3.6
 */
public class PropsExpressionContext implements Function<String,Object>, ReturnGuidance, PropertiesGuidance {
    private final Properties main;
    private final Properties target;
    private final String refKey;
    private final boolean useDef;

    public PropsExpressionContext(Properties main, Properties target, String refKey, boolean useDef) {
        this.main = main;
        this.target = target;
        this.refKey = refKey;
        this.useDef = useDef;
    }

    @Override
    public Object apply(String name) {
        if (name.indexOf('.') == 0 && refKey != null) {
            //本级引用
            int refIdx = refKey.lastIndexOf('.');
            if (refIdx > 0) {
                name = refKey.substring(0, refIdx) + name;
            }
        }

        String val = null;

        if (target != null) {
            //从"目标属性"获取
            val = target.getProperty(name);
        }

        if (val == null) {
            //从"主属性"获取
            val = main.getProperty(name);

            if (val == null) {
                //从"环镜变量"获取
                val = System.getenv(name);
            }
        }

        return val;
    }

    @Override
    public boolean isReturnNull() {
        return true;
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public boolean allowPropertyDefault() {
        return useDef;
    }
}