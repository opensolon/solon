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

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.flow.driver.SimpleFlowDriver;

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
    private final String id;
    private final String title;
    private final ChainDriver driver;

    private final Map<String, Element> elements = new HashMap<>();

    private Element start;

    public Chain(String id) {
        this(id, null, null);
    }

    public Chain(String id, String title, ChainDriver driver) {
        this.id = id;
        this.title = (title == null ? id : title);

        this.driver = (driver == null ? SimpleFlowDriver.getInstance() : driver);
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
     * 驱动器
     */
    public ChainDriver driver() {
        return driver;
    }

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
    public void addNode(String id, String title, ElementType type) {
        addNode(id, title, type, null, null);
    }

    /**
     * 添加节点
     */
    public void addNode(String id, String title, ElementType type, Map<String, Object> meta, String taskExpr) {
        //不能是线
        assert type != ElementType.line;

        addElement(id, title, type, null, null, meta, null, taskExpr);
    }

    /**
     * 添加线
     */
    public void addLine(String id, String title, String prveId, String nextId) {
        addLine(id, title, prveId, nextId, null, null);
    }

    /**
     * 添加线
     */
    public void addLine(String id, String title, String prveId, String nextId, Map<String, Object> meta, String conditionExpr) {
        addElement(id, title, ElementType.line, prveId, nextId, meta, conditionExpr, null);
    }

    /**
     * 添加元素
     */
    protected void addElement(String id, String title, ElementType type, String prveId, String nextId, Map<String, Object> meta, String conditionExpr, String taskExpr) {
        Element element = new Element(this, id, title, type, meta, prveId, nextId, conditionExpr, taskExpr);

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

    public static Chain parse(String json) {
        ONode oNode = ONode.load(json);

        String id = oNode.get("id").getString();
        String title = oNode.get("id").getString();
        String driverStr = oNode.get("driver").getString();
        ChainDriver driver = (Utils.isEmpty(driverStr) ? null : ClassUtil.tryInstance(driverStr));

        Chain chain = new Chain(id, title, driver);

        for (ONode n1 : oNode.get("elements").ary()) {
            ElementType type = ElementType.nameOf(n1.get("type").getString());
            if (type == ElementType.line) {
                chain.addLine(n1.get("id").getString(),
                        n1.get("title").getString(),
                        n1.get("prveId").getString(),
                        n1.get("nextId").getString(),
                        n1.get("meta").toObject(Map.class),
                        n1.get("condition").getString());
            } else {
                chain.addNode(n1.get("id").getString(),
                        n1.get("title").getString(),
                        type,
                        n1.get("meta").toObject(Map.class),
                        n1.get("task").getString());
            }
        }

        return chain;
    }
}