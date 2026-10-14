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
package org.noear.nami;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nami - 请求上下文
 *
 * @author noear
 * @since 1.4
 */
public class Context {
    /**
     * 配置
     */
    public final Config config;
    /**
     * 目标
     */
    public final Object target;
    /**
     * 函数
     */
    public final Method method;

    /**
     * 动作（GET,POST...）
     */
    public final String action;

    /**
     * 请求地址
     */
    public final String url;
    /**
     * 请求地址
     */
    public final URI uri;

    /**
     * 请求头信息
     */
    public final Map<String, String> headers = new LinkedHashMap<>();
    /**
     * 请求参数
     */
    public final Map<String, Object> args = new LinkedHashMap<>();

    /**
     * 请求主体
     */
    public final Object body;


    public Context(Config config, Object target, Method method, String action, String url, Object body) {
        this.config = config;
        this.target = target;
        this.method = method;
        this.action = action;
        this.url = url;
        this.uri = URI.create(url);
        this.body = body;
    }

    public Object bodyOrArgs() {
        if (body == null) {
            return args;
        } else {
            return body;
        }
    }
}
