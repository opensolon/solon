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
package org.noear.solon.ai.chat.function;

import org.noear.solon.Utils;
import org.noear.solon.ai.chat.annotation.FunctionMapping;
import org.noear.solon.ai.chat.annotation.FunctionParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于方法构建的聊天函数
 *
 * @author noear
 * @since 3.1
 */
public class MethodChatFunction implements ChatFunction {
    private final Object target;
    private final Method method;
    private final String description;
    private final String name;
    private final List<ChatFunctionParam> params;

    public MethodChatFunction(Object target, Method method) {
        this.target = target;
        this.method = method;

        FunctionMapping m1Anno = method.getAnnotation(FunctionMapping.class);
        this.name = Utils.annoAlias(m1Anno.name(), method.getName());
        this.description = m1Anno.description();

        this.params = new ArrayList<>();

        for (Parameter p1 : method.getParameters()) {
            FunctionParam p1Anno = p1.getAnnotation(FunctionParam.class);

            String name = Utils.annoAlias(p1Anno.name(), p1.getName());
            params.add(new ChatFunctionParamDecl(name, p1.getType(), p1Anno.required(), p1Anno.description()));
        }
    }

    /**
     * 函数名字
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * 函数描述
     */
    @Override
    public String description() {
        return description;
    }

    /**
     * 函数参数
     */
    @Override
    public Iterable<ChatFunctionParam> params() {
        return params;
    }

    /**
     * 执行处理
     */
    @Override
    public String handle(Map<String, Object> args) throws Throwable {
        Object[] vals = new Object[params.size()];

        for (int i = 0; i < params.size(); ++i) {
            vals[i] = args.get(params.get(i).name());
        }

        Object rst = method.invoke(target, vals);
        return String.valueOf(rst);
    }
}