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
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.flow.driver.SimpleFlowDriver;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    private final Map<String, Node> nodes = new HashMap<>();
    private final List<Link> links = new ArrayList<>();

    private Node start;

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
    public Node start() {
        return start;
    }

    /**
     * 获取所有元素
     */
    public Map<String, Node> nodes() {
        return Collections.unmodifiableMap(nodes);
    }

    /**
     * 获取所有连接
     */
    public List<Link> links() {
        return Collections.unmodifiableList(links);
    }


    /**
     * 添加节点
     */
    public void addNode(String id, String title, NodeType type, List<LinkDecl> linkDecls) {
        addNode(id, title, type, linkDecls, null, null);
    }

    /**
     * 添加节点
     */
    public void addNode(String id, String title, NodeType type, List<LinkDecl> linkDecls, Map<String, Object> meta, String taskExpr) {
        List<Link> linkTmp = new ArrayList<>();

        if (linkDecls != null) {
            for (LinkDecl linkSpec : linkDecls) {
                linkTmp.add(new Link(this, id, linkSpec));
            }
        }

        links.addAll(linkTmp);

        Node element = new Node(this, id, title, type, linkTmp, meta, taskExpr);

        nodes.put(element.id(), element);

        if (type == NodeType.start) {
            start = element;
        }

    }

    /**
     * 查找一个元素
     */
    public Node selectById(String id) {
        return nodes.get(id);
    }

    public static Chain parseByUri(String uri) throws IOException {
        URL url = ResourceUtil.findResource(uri, false);
        assert url != null;

        return parseByJson(ResourceUtil.getResourceAsString(url, Solon.encoding()));
    }

    public static Chain parseByJson(String json) {
        ONode oNode = ONode.load(json);

        String id = oNode.get("id").getString();
        String title = oNode.get("id").getString();
        String driverStr = oNode.get("driver").getString();
        ChainDriver driver = (Utils.isEmpty(driverStr) ? null : ClassUtil.tryInstance(driverStr));

        Chain chain = new Chain(id, title, driver);

        for (ONode n1 : oNode.get("nodes").ary()) {
            NodeType type = NodeType.nameOf(n1.get("type").getString());
            List<LinkDecl> linkDecls = new ArrayList<>();

            for (ONode ls1 : n1.get("links").ary()) {
                linkDecls.add(new LinkDecl(
                        ls1.get("toId").getString(),
                        ls1.get("title").getString(),
                        ls1.get("meta").toObject(Map.class),
                        n1.get("condition").getString()
                ));
            }

            chain.addNode(n1.get("id").getString(),
                    n1.get("title").getString(),
                    type,
                    linkDecls,
                    n1.get("meta").toObject(Map.class),
                    n1.get("task").getString());
        }

        return chain;
    }
}