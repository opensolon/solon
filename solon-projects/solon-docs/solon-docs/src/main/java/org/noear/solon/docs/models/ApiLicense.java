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
package org.noear.solon.docs.models;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口许可证
 *
 * @author noear
 * @since 2.4
 */
public class ApiLicense implements Serializable {
    private String name;
    private String url;
    private Map<String, Object> vendorExtensions = new LinkedHashMap();

    public ApiLicense(){
        //用于反序列化
    }

    public ApiLicense name(String name) {
        this.name = name;
        return this;
    }

    public ApiLicense url(String url) {
        this.url = url;
        return this;
    }


    public String name(){
        return this.name;
    }

    public String url(){
        return this.url;
    }

    public Map<String, Object> vendorExtensions(){
        return vendorExtensions;
    }
}
