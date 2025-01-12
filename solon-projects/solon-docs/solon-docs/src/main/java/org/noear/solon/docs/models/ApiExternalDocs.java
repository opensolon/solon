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

/**
 * 接口扩展文档
 *
 * @author noear
 * @since 2.2
 */
public class ApiExternalDocs implements Serializable {
    private String description;
    private String url;

    public String description(){
        return description;
    }

    public String url(){
        return url;
    }

    public ApiExternalDocs() {
        //用于反序列化
    }

    public ApiExternalDocs(String description, String url) {
        this.description = description;
        this.url = url;
    }
}
