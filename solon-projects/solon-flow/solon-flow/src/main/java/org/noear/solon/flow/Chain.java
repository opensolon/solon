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
package org.noear.solon.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 链
 *
 * @author noear
 * @since 3.0
 * */
public class Chain {
    private Node _start;
    private Map<String, Node> _nodes = new HashMap<>();

    /**
     * 获取起始节点
     */
    public Node start() {
        return _start;
    }

    /**
     * 获取所有节点
     */
    public Map<String, Node> nodes() {
        return _nodes;
    }

    /**
     * 添加节点
     */
    public void addNode(String id, String name, int type, String tasks) {
        addElement(id, name, type, null, null, null, tasks);

    }

    /**
     * 添加线
     */
    public void addLine(String id, String name, String prveId, String nextId, String conditions) {
        addElement(id, name, NodeType.line, prveId, nextId, conditions, null);
    }

    /**
     * 添加元素
     */
    protected void addElement(String id, String name, int type, String prveId, String nextId, String conditions, String tasks) {
        Node node = new Node(this);

        node._conditions_str = conditions;
        node._tasks_str = tasks;

        node._id = id;
        node._name = name;
        node._type = type;
        node._prveId = prveId; //仅line才有
        node._nextId = nextId; //仅line才有

        _nodes.put(node.id(), node);

        if (type == 0) {
            _start = node;
        }

    }

    //查找一个节点
    protected Node selectById(String id) {
        return nodes().get(id);
    }

    //查找前面的节点
    protected List<Node> selectByNextId(String id) {
        List<Node> nodes = new ArrayList<>();

        for (Node n : nodes().values()) {
            if (id.equals(n.nextId())) {
                nodes.add(n);
            }
        }

        return nodes;
    }

    //查找后面的节点
    protected List<Node> selectByPrveId(String id) {
        List<Node> nodes = new ArrayList<>();

        for (Node n : nodes().values()) {
            if (id.equals(n.prveId())) {
                nodes.add(n);
            }
        }

        return nodes;
    }
}
