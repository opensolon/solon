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
package org.noear.solon.core.handle;

/**
 * 小饼
 *
 * @author noear
 * @since 3.0
 */
public class Cookie {
    public final String name;
    public final String value;
    public String domain;
    public String path;
    public int maxAge;
    public boolean secure;
    public boolean httpOnly;

    /**
     * @param name  名称
     * @param value 值
     */
    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
        this.path = "/";
        this.maxAge = -1;
    }

    /**
     * 域
     */
    public Cookie domain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * 路径
     */
    public Cookie path(String path) {
        this.path = path;
        return this;
    }

    /**
     * 过期时间
     */
    public Cookie maxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    /**
     * 安全标志
     */
    public Cookie secure(boolean secure) {
        this.secure = secure;
        return this;
    }

    /**
     * httpOnly 标志
     */
    public Cookie httpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }
}