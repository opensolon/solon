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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 节点申明
 *
 * @author noear
 * @since 3.0
 */
public class NodeDecl {
    private final String id;
    private String title;
    private NodeType type;      //元素类型
    private Map<String, Object> meta; //元信息
    private List<LinkDecl> links = new ArrayList<>();
    private String taskExpr;

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public NodeType type() {
        return type;
    }

    public Map<String, Object> meta() {
        return meta;
    }

    public List<LinkDecl> links() {
        return links;
    }

    public String task() {
        return taskExpr;
    }

    /////////////////

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

    public NodeDecl link(LinkDecl link) {
        this.links.add(link);
        return this;
    }

    public NodeDecl linkTo(String toId) {
        return link(new LinkDecl(toId));
    }

    public NodeDecl task(String taskExpr) {
        this.taskExpr = taskExpr;
        return this;
    }
}
