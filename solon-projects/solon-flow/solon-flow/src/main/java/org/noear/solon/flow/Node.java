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

import org.noear.solon.Utils;

import java.util.*;

/**
 * 节点
 *
 * @author noear
 * @since 3.0
 * */
public class Node {
    private final transient Chain chain;

    private final NodeDecl decl;
    private final List<Link> nextLinks = new ArrayList<>(); //as nextLinks

    private List<Node> prveNodes, nextNodes;
    private List<Link> prveLinks;
    private Condition when;
    private Task task;

    protected Node(Chain chain, NodeDecl decl, List<Link> links) {
        this.chain = chain;
        this.decl = decl;

        if (links != null) {
            this.nextLinks.addAll(links);
            //按优先级排序
            Collections.sort(nextLinks);
        }
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
        return decl.id;
    }

    /**
     * 显示标题
     */
    public String title() {
        return decl.title;
    }

    /**
     * 类型
     */
    public NodeType type() {
        return decl.type;
    }

    /**
     * 元信息
     */
    public Map<String, Object> meta() {
        return Collections.unmodifiableMap(decl.meta);
    }

    /**
     * 获取元信息
     */
    public <T> T meta(String key) {
        return (T) decl.meta.get(key);
    }

    /**
     * 获取元信息或默认
     */
    public <T> T metaOrDefault(String key, T def) {
        return (T) decl.meta.getOrDefault(key, def);
    }

    /**
     * 前面的连接（流入连接）
     */
    public List<Link> prveLinks() {
        if (prveLinks == null) {
            List<Link> tmp = new ArrayList<>();

            if (type() != NodeType.start) {
                for (Link l : chain.links()) {
                    if (id().equals(l.nextId())) { //by nextID
                        tmp.add(l);
                    }
                }

                //按优先级排序
                Collections.reverse(tmp);
            }

            prveLinks = Collections.unmodifiableList(tmp);
        }

        return prveLinks;
    }

    /**
     * 后面的连接（流出连接）
     */
    public List<Link> nextLinks() {
        return Collections.unmodifiableList(nextLinks);
    }

    /**
     * 前面的节点
     */
    public List<Node> prveNodes() {
        if (prveNodes == null) {
            List<Node> tmp = new ArrayList<>();

            if (type() != NodeType.start) {
                for (Link l : chain.links()) { //要从链处找
                    if (id().equals(l.nextId())) {
                        tmp.add(chain.getNode(l.prveId()));
                    }
                }
            }
            prveNodes = Collections.unmodifiableList(tmp);
        }

        return prveNodes;
    }

    /**
     * 后面的节点
     */
    public List<Node> nextNodes() {
        if (nextNodes == null) {
            List<Node> tmp = new ArrayList<>();

            if (type() != NodeType.end) {
                for (Link l : this.nextLinks()) { //从自由处找
                    tmp.add(chain.getNode(l.nextId()));
                }
            }
            nextNodes = Collections.unmodifiableList(tmp);
        }

        return nextNodes;
    }

    /**
     * 后面的节点（一个）
     */
    public Node nextNode() {
        if (nextNodes().size() > 0) {
            return nextNodes().get(0);
        } else {
            return null;
        }
    }

    /**
     * 任务条件
     */
    public Condition when() {
        if (when == null) {
            when = new Condition(decl.when);
        }

        return when;
    }

    /**
     * 任务
     */
    public Task task() {
        if (task == null) {
            task = new Task(this, decl.task);
        }

        return task;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("{");
        buf.append("id='").append(decl.id).append('\'');
        buf.append(", type='").append(decl.type).append('\'');

        if (Utils.isNotEmpty(decl.title)) {
            buf.append(", title='").append(decl.title).append('\'');
        }

        if (Utils.isNotEmpty(decl.when)) {
            buf.append(", when='").append(decl.when).append('\'');
        }

        if (Utils.isNotEmpty(decl.task)) {
            buf.append(", task='").append(decl.task).append('\'');
        }

        if (Utils.isNotEmpty(decl.links)) {
            buf.append(", link=").append(decl.links);
        }

        if (Utils.isNotEmpty(decl.meta)) {
            buf.append(", meta=").append(decl.meta);
        }

        buf.append("}");

        return buf.toString();
    }
}