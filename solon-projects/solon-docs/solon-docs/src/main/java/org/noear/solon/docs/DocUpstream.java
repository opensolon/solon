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
package org.noear.solon.docs;

import java.io.Serializable;
import java.net.URI;

/**
 * 文档上游
 *
 * @author noear
 * @since 2.8
 */
public class DocUpstream implements Serializable {
    private URI target;
    private String contextPath;
    private String uri;

    public DocUpstream() {
        //用于反序列化
    }

    public DocUpstream(URI target, String contextPath, String uri) {
        this.target = target;
        this.uri = uri;
        this.contextPath = contextPath;
    }

    /**
     * 获取服务（addr or name）
     */
    public URI getTarget() {
        return target;
    }

    /**
     * 获取资源组上下文路径
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * 获取路径
     */
    public String getUri() {
        return uri;
    }
}