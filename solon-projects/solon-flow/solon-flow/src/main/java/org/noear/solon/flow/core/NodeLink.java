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

import org.noear.solon.Utils;

import java.util.Map;

/**
 * 链接
 *
 * @author noear
 * @since 3.0
 */
public class NodeLink implements Comparable<NodeLink> {
    private final Chain chain;
    private final String fromId;
    private final NodeLinkDecl decl;

    private Node prveNode, nextNode;
    private Condition condition;

    public NodeLink(Chain chain, String fromId, NodeLinkDecl decl) {
        this.chain = chain;
        this.fromId = fromId;
        this.decl = decl;
    }

    /**
     * 所属链
     */
    public Chain chain() {
        return chain;
    }

    /**
     * 元信息
     */
    public Map<String, Object> meta() {
        return decl.meta;
    }

    /**
     * 条件
     */
    public Condition condition() {
        if (condition == null) {
            condition = new Condition(this, decl.condition);
        }

        return condition;
    }

    /**
     * 前面的节点Id
     */
    public String prveId() {
        return fromId;
    }

    /**
     * 后面的节点Id
     */
    public String nextId() {
        return decl.toId;
    }

    /**
     * 前面的节点
     */
    public Node prveNode() {
        if (prveNode == null) {
            prveNode = chain.getNode(prveId()); //by id query
        }

        return prveNode;
    }

    /**
     * 后面的节点
     */
    public Node nextNode() {
        if (nextNode == null) {
            nextNode = chain.getNode(nextId()); //by id query
        }

        return nextNode;
    }

    @Override
    public int compareTo(NodeLink o) {
        if (this.decl.priority > o.decl.priority) {
            return -1; //大的在前
        } else if (this.decl.priority < o.decl.priority) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("{");
        buf.append("priority=").append(decl.priority);

        if (Utils.isNotEmpty(decl.title)) {
            buf.append(", title='").append(decl.title).append('\'');
        }

        buf.append(", prveId='").append(prveId()).append('\'');
        buf.append(", nextId='").append(nextId()).append('\'');

        if (Utils.isNotEmpty(decl.meta)) {
            buf.append(", meta=").append(decl.meta);
        }

        if (Utils.isNotEmpty(decl.condition)) {
            buf.append(", condition=").append(decl.condition);
        }

        buf.append("}");

        return buf.toString();
    }
}