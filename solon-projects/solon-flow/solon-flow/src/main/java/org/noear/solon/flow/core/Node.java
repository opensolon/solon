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

import org.noear.solon.core.util.PredicateEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 节点
 *
 * @author noear
 * @since 3.0
 * */
public class Node {
    private final transient Chain chain;

    private final NodeDecl decl;
    private final List<Link> links;

    private List<Node> prveNodes, nextNodes;
    private List<Link> prveLines, nextLines;
    private Task task;

    protected Node(Chain chain, NodeDecl decl, List<Link> links) {
        this.chain = chain;
        this.decl = decl;
        this.links = links;
    }


    /**
     * 所属链
     */
    public Chain chain() {
        return chain;
    }

    /**
     * 标识
     */
    public String id() {
        return decl.id();
    }

    /**
     * 显示标题
     */
    public String title() {
        return decl.title();
    }

    /**
     * 类型
     */
    public NodeType type() {
        return decl.type();
    }

    /**
     * 元信息
     */
    public Map<String, Object> meta() {
        return Collections.unmodifiableMap(decl.meta());
    }

    /**
     * 连接
     */
    public List<Link> links() {
        return Collections.unmodifiableList(links);
    }


    /**
     * 前面的节点
     */
    public List<Node> prveNodes() {
        if (prveNodes == null) {
            prveNodes = new ArrayList<>();

            if (type() != NodeType.start) {
                for (Link l : chain.links()) { //要从链处找
                    if (id().equals(l.nextId())) {
                        nextNodes.add(chain.selectById(l.prveId()));
                    }
                }
            }
        }

        return prveNodes;
    }

    /**
     * 后面的节点
     */
    public List<Node> nextNodes() {
        if (nextNodes == null) {
            nextNodes = new ArrayList<>();

            if (type() != NodeType.end) {
                for (Link l : this.links()) { //从自由处找
                    nextNodes.add(chain.selectById(l.nextId()));
                }
            }
        }

        return nextNodes;
    }

    /**
     * 后面的节点（一个）
     */
    public Node nextNode() {
        return nextNodes().get(0);
    }

    /**
     * 前面的线
     */
    public List<Link> prveLines() {
        if (prveLines == null) {
            prveLines = new ArrayList<>();

            if (type() != NodeType.start) {
                for (Link l : chain.links()) {
                    if (id().equals(l.nextId())) { //by nextID
                        prveLines.add(l);
                    }
                }
            }
        }

        return prveLines;
    }

    /**
     * 后面的线
     */
    public List<Link> nextLines() {
        if (links == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(links);
        }
    }

    /**
     * 任务
     */
    public Task task() {
        if (task == null) {
            task = new Task(this, decl.task());
        }

        return task;
    }

    @Override
    public String toString() {
        if (type() == NodeType.execute) {
            return "{" +
                    "id='" + decl.id() + '\'' +
                    ", title='" + decl.title() + '\'' +
                    ", type=" + decl.type() +
                    ", meta=" + decl.meta() +
                    ", task='" + decl.task() + '\'' +
                    '}';
        } else {
            return "{" +
                    "id='" + decl.id() + '\'' +
                    ", title='" + decl.title() + '\'' +
                    ", type=" + decl.type() +
                    ", meta=" + decl.meta() +
                    '}';
        }
    }
}