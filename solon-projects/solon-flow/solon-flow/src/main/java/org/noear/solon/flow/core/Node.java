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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
* 存储设计::
*
* 0开始节点={id:n1, type:0, title:'', }
*
* 1连接线段={id:l1, type:1, title:'', prveId:'n1', nextId:'n2', meta:{}, condition:'(m.user_id,>,12) && (m,F,$ssss(m))'} //A=and,O=or,E=end
*
* 2执行节点={id:n2, type:2, title:'', meta:{}, task:'F,tag/fun1;R,tag/rule1'}
*
* 3包容网关={id:n3, type:3, title:'', }
* 4排他网关={id:n4, type:4, title:'', }
* 5并行网关={id:n5, type:5, title:'', }
* 6汇聚网关={id:n6, type:6, title:'', }
*
* 9结束节点={id:n9, type:9, title:'', }
* */

/**
 * 元素对象（节点或线）
 *
 * @author noear
 * @since 3.0
 * */
public class Node {
    private final transient Chain chain;

    private final String id;
    private final String title;
    private final NodeType type;      //元素类型
    private final Map<String, Object> meta; //元信息
    private final List<Link> links;

    private final String taskExpr;

    private List<Node> prveNodes, nextNodes;
    private List<Link> prveLines, nextLines;
    private Condition condition;
    private Task task;

    protected Node(Chain chain, String id, String title, NodeType type, List<Link> links, Map<String, Object> meta, String taskExpr) {
        this.chain = chain;

        this.id = id;
        this.title = (title == null ? id : title);
        this.type = type;
        this.meta = meta;
        this.links = links;

        this.taskExpr = taskExpr;
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
        return id;
    }

    /**
     * 显示标题
     */
    public String title() {
        return title;
    }

    /**
     * 类型
     */
    public NodeType type() {
        return type;
    }

    /**
     * 连接
     */
    public List<Link> links() {
        return links;
    }


    /**
     * 前面的节点
     */
    public List<Node> prveNodes() {
        if (prveNodes == null) {
            prveNodes = new ArrayList<>();

            if (type != NodeType.start) {
                for (Link l : chain.links()) { //要从链处找
                    if (id.equals(l.nextId())) {
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

            if (type != NodeType.end) {
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

            if (type != NodeType.start) {
                for (Link l : chain.links()) {
                    if (id.equals(l.nextId())) { //by nextID
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
            task = new Task(this, taskExpr);
        }

        return task;
    }

    @Override
    public String toString() {
        if (type == NodeType.execute) {
            return "{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", meta=" + meta +
                    ", task='" + taskExpr + '\'' +
                    '}';
        } else {
            return "{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", meta=" + meta +
                    '}';
        }
    }
}