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
import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法描述（预处理并缓存）
 *
 * @author noear
 * @since 1.2
 */
public class MethodDesc {
    private static final Map<Method, MethodDesc> cached = new ConcurrentHashMap<>();

    public static MethodDesc get(Method method) {
        MethodDesc mw = cached.computeIfAbsent(method, k -> new MethodDesc(method));
        return mw;
    }

    /// ////////////////

    private final Method method;
    private final List<ParameterDesc> parameters;
    private final Map<String, String> headers;
    private String action; //method
    private String path;

    /**
     * 方法
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 参数
     */
    public List<ParameterDesc> getParameters() {
        return parameters;
    }

    /**
     * 头信息
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 动作
     */
    public String getAction() {
        return action;
    }

    /**
     * 路径
     */
    public String getPath() {
        return path;
    }

    protected MethodDesc(Method m) {
        this.method = m;
        this.parameters = new ArrayList<>(m.getParameterCount());
        this.headers = new HashMap<>();

        if (resolveMethodAnnoByNamiMapping(m) == false) {
            resolveMethodAnnoByMapping(m);
        }

        resolveParamAnno(m);
    }

    protected boolean resolveMethodAnnoByNamiMapping(Method m) {
        NamiMapping mappingAnno = m.getAnnotation(NamiMapping.class);

        if (mappingAnno != null) {
            //格式1: GET
            //格式2: GET user/a.0.1
            if (mappingAnno.value().length() > 0) {
                String val = mappingAnno.value().trim();
                int idx = val.indexOf(" ");

                if (idx > 0) {
                    action = val.substring(0, idx);
                    path = val.substring(idx + 1);
                } else {
                    action = val;
                }
            }

            if (mappingAnno.headers().length > 0) {
                for (String h : mappingAnno.headers()) {
                    String[] ss = h.split("=");
                    if (ss.length == 2) {
                        headers.put(ss[0].trim(), ss[1].trim());
                    }
                }
            }
        }

        return mappingAnno != null;
    }

    protected void resolveMethodAnnoByMapping(Method m) {
        Mapping mappingAnno = m.getAnnotation(Mapping.class);

        if (mappingAnno != null) {
            //格式1: GET
            //格式2: GET user/a.0.1
            if (mappingAnno.value().length() > 0) {
                if (mappingAnno.method().length > 0) {
                    action = mappingAnno.method()[0].name();
                }
                path = Utils.annoAlias(mappingAnno.value(), mappingAnno.path());
            }

            if (mappingAnno.headers().length > 0) {
                for (String h : mappingAnno.headers()) {
                    String[] ss = h.split("=");
                    if (ss.length == 2) {
                        headers.put(ss[0].trim(), ss[1].trim());
                    }
                }
            }
        }

        if (m.isAnnotationPresent(Post.class)) {
            action = MethodType.POST.name();
        }

        if (m.isAnnotationPresent(Get.class)) {
            action = MethodType.GET.name();
        }

        if (m.isAnnotationPresent(Put.class)) {
            action = MethodType.PUT.name();
        }

        if (m.isAnnotationPresent(Delete.class)) {
            action = MethodType.DELETE.name();
        }

        if (m.isAnnotationPresent(Patch.class)) {
            action = MethodType.PATCH.name();
        }

        Consumes anno = m.getAnnotation(Consumes.class);
        if (anno != null) {
            headers.put(ContentTypes.HEADER_CONTENT_TYPE, anno.value());
        }
    }

    /**
     * 分析参数数注解
     */
    protected void resolveParamAnno(Method m) {
        for (Parameter p1 : m.getParameters()) {
            ParameterDesc p1w = new ParameterDesc(p1);

            parameters.add(p1w);

            if (p1w.isBody()) {
                NamiBody namiBodyAnno = p1.getAnnotation(NamiBody.class);
                if (namiBodyAnno.contentType().length() > 0) {
                    headers.putIfAbsent(ContentTypes.HEADER_CONTENT_TYPE, namiBodyAnno.contentType());
                }
            }

            if (File.class.isAssignableFrom(p1.getType()) || UploadedFile.class.isAssignableFrom(p1.getType())) {
                headers.put(ContentTypes.HEADER_CONTENT_TYPE, ContentTypes.FORM_DATA_VALUE);
            }
        }
    }
}