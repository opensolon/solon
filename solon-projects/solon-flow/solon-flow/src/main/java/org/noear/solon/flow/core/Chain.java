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
    private Element start;
    private Map<String, Element> elements = new HashMap<>();

    /**
     * 获取起始节点
     */
    public Element start() {
        return start;
    }

    /**
     * 获取所有元素
     */
    public Map<String, Element> elements() {
        return elements;
    }

    /**
     * 添加节点
     */
    public void addNode(String id, String name, ElementType type, String tasks) {
        //不能是线
        assert type != ElementType.line;

        addElement(id, name, type, null, null, null, tasks);
    }

    /**
     * 添加线
     */
    public void addLine(String id, String name, String prveId, String nextId, String conditions) {
        addElement(id, name, ElementType.line, prveId, nextId, conditions, null);
    }

    /**
     * 添加元素
     */
    protected void addElement(String id, String name, ElementType type, String prveId, String nextId, String conditions, String tasks) {
        Element element = new Element(this);

        element.conditionsExpr = conditions;
        element.tasksExpr = tasks;

        element.id = id;
        element.name = name;
        element.type = type;
        element.prveId = prveId; //仅line才有
        element.nextId = nextId; //仅line才有

        elements.put(element.id(), element);

        if (type == ElementType.start) {
            start = element;
        }

    }

    /**
     * 查找一个元素
     */
    public Element selectById(String id) {
        return elements().get(id);
    }

    /**
     * 查找前面的节点
     */
    public List<Element> selectByNextId(String id) {
        List<Element> nodes = new ArrayList<>();

        for (Element n : elements().values()) {
            if (id.equals(n.nextId())) {
                nodes.add(n);
            }
        }

        return nodes;
    }

    /**
     * 查找后面的节点
     */
    public List<Element> selectByPrveId(String id) {
        List<Element> nodes = new ArrayList<>();

        for (Element n : elements().values()) {
            if (id.equals(n.prveId())) {
                nodes.add(n);
            }
        }

        return nodes;
    }
}