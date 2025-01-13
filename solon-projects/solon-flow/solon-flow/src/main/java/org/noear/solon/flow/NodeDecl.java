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
package org.noear.solon.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 节点申明
 *
 * @author noear
 * @since 3.0
 */
public class NodeDecl {
    protected final String id;
    protected String title;
    protected NodeType type;      //元素类型
    protected Map<String, Object> meta; //元信息
    protected List<NodeLinkDecl> links = new ArrayList<>();
    protected String task;


    /// //////////////

    /**
     * @param id   标识
     * @param type 类型
     */
    public NodeDecl(String id, NodeType type) {
        this.id = id;
        this.type = type;
    }

    /**
     * 配置标题
     */
    public NodeDecl title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 配置元信息
     */
    public NodeDecl meta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    /**
     * 配置元信息
     */
    public NodeDecl metaPut(String key, Object value) {
        if (meta == null) {
            meta = new HashMap<>();
        }

        meta.put(key, value);
        return this;
    }

    /**
     * 配置链接
     */
    public NodeDecl linkAdd(String nextId, Consumer<NodeLinkDecl> configure) {
        NodeLinkDecl linkDecl = new NodeLinkDecl(nextId);
        if (configure != null) {
            configure.accept(linkDecl);
        }
        this.links.add(linkDecl);
        return this;
    }

    /**
     * 配置链接
     */
    public NodeDecl linkAdd(String nextId) {
        return linkAdd(nextId, null);
    }

    /**
     * 配置任务
     */
    public NodeDecl task(String task) {
        this.task = task;
        return this;
    }
}
