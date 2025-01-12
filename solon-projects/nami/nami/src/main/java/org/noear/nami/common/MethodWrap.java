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
package org.noear.nami.common;

import org.noear.nami.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 函数包装器（预处理并缓存）
 *
 * @author noear
 * @since 1.2
 */
public class MethodWrap {
    private static final Map<Method, MethodWrap> cached = new ConcurrentHashMap<>();

    public static MethodWrap get(Method method) {
        MethodWrap mw = cached.computeIfAbsent(method, k -> new MethodWrap(method));
        return mw;
    }

    protected void resolveMappingAnno(Method m) {
        mappingAnno = m.getAnnotation(NamiMapping.class);
    }

    protected void resolveBodyAnno(Parameter p1) {
        bodyAnno = p1.getAnnotation(NamiBody.class);
    }


    protected MethodWrap(Method m) {
        this.method = m;
        this.parameters = m.getParameters();
        resolveMappingAnno(m);

        for (Parameter p1 : parameters) {
            resolveBodyAnno(p1);
            if (bodyAnno != null) {
                bodyName = p1.getName();
                break;
            }
        }

        if (mappingAnno != null) {
            //格式1: GET
            //格式2: GET user/a.0.1
            if (mappingAnno.value().length() > 0) {
                String val = mappingAnno.value().trim();
                int idx = val.indexOf(" ");

                if (idx > 0) {
                    act = val.substring(0, idx);
                    fun = val.substring(idx + 1);
                } else {
                    act = val;
                }
            }

            if (mappingAnno.headers().length > 0) {
                mappingHeaders = new HashMap<>();

                for (String h : mappingAnno.headers()) {
                    String[] ss = h.split("=");
                    if (ss.length == 2) {
                        mappingHeaders.put(ss[0].trim(), ss[1].trim());
                    }
                }
            }
        }
    }

    private Method method;
    private Parameter[] parameters;
    private String bodyName;
    private NamiBody bodyAnno;
    private NamiMapping mappingAnno;
    private Map<String, String> mappingHeaders;
    private String act;
    private String fun;

    public Method getMethod() {
        return method;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public String getBodyName() {
        return bodyName;
    }

    public NamiBody getBodyAnno() {
        return bodyAnno;
    }

    public NamiMapping getMappingAnno() {
        return mappingAnno;
    }

    public Map<String, String> getMappingHeaders() {
        return mappingHeaders;
    }

    public String getAct() {
        return act;
    }

    public String getFun() {
        return fun;
    }
}
