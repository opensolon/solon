/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.flow.core;

import java.util.Map;

/**
 * @author noear
 * @since 3.0
 */
public class LinkDecl {
    private String toId;
    private String title;
    private Map<String, Object> meta;
    private String condition;

    public LinkDecl(String toId) {
        this.toId = toId;
    }

    public LinkDecl(String toId, String title, Map<String, Object> meta, String condition) {
        this.toId = toId;
        this.title = title;
        this.meta = meta;
        this.condition = condition;
    }

    public String toId() {
        return toId;
    }

    public String title() {
        return title;
    }

    public Map<String, Object> meta() {
        return meta;
    }

    public String condition() {
        return condition;
    }

    @Override
    public String toString() {
        return "LinkSpec{" +
                "toId='" + toId + '\'' +
                ", title='" + title + '\'' +
                ", meta=" + meta +
                ", condition='" + condition + '\'' +
                '}';
    }
}
