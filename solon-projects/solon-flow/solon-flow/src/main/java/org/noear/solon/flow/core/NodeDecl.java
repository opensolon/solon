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
package org.noear.solon.flow.core;

import java.util.ArrayList;
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

    public NodeDecl(String id, NodeType type) {
        this.id = id;
        this.type = type;
    }

    public NodeDecl title(String title) {
        this.title = title;
        return this;
    }

    public NodeDecl meta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public NodeDecl link(String toId, Consumer<NodeLinkDecl> configure) {
        NodeLinkDecl linkDecl = new NodeLinkDecl(toId);
        if (configure != null) {
            configure.accept(linkDecl);
        }
        this.links.add(linkDecl);
        return this;
    }

    public NodeDecl link(String toId) {
        return link(toId, null);
    }

    public NodeDecl task(String task) {
        this.task = task;
        return this;
    }
}
