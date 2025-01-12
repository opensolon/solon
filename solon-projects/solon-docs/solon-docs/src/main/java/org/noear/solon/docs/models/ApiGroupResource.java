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

import org.noear.solon.Utils;

import java.io.Serializable;

/**
 * 接口组资源
 *
 * @author noear
 * @since 2.2
 */
public class ApiGroupResource implements Serializable {
    private String name;
    private String url;
    private String location;
    private String swaggerVersion;
    private String contextPath;

    public ApiGroupResource(String name, String version, String url, String contextPath) {
        this.name = name;
        this.url = url;
        this.location = url;
        this.swaggerVersion = version;

        if (Utils.isNotEmpty(contextPath)) {
            if (contextPath.startsWith("/")) {
                this.contextPath = contextPath;
            } else {
                this.contextPath = "/" + contextPath;
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getLocation() {
        return location;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }

    public String getContextPath() {
        return contextPath;
    }
}
