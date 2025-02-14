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
package org.noear.solon.ai.chat.functioncall;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.annotation.FunctionMapping;
import org.noear.solon.ai.annotation.FunctionParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
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

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Iterable<ChatFunctionParam> params() {
        return params;
    }

    @Override
    public String handle(Map<String, Object> args) throws Throwable {
        Object[] vals = new Object[params.size()];

        //用 ONode 可以自动转换类型
        ONode argsNode = ONode.load(args);
        for (int i = 0; i < params.size(); ++i) {
            ONode v1 = argsNode.getOrNull(params.get(i).name());
            if (v1 == null) {
                vals[i] = null;
            } else {
                vals[i] = v1.toObject(params.get(i).type());
            }
        }

        Object rst = method.invoke(target, vals);
        return String.valueOf(rst);
    }
}