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

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.lang.Preview;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 链
 *
 * @author noear
 * @since 3.0
 * */
@Preview("3.0")
public class Chain {
    private final String id;
    private final String title;
    private final String driver;
    private final Map<String, Object> meta = new HashMap<>(); //元信息
    private final Map<String, Node> nodes = new HashMap<>();

    private final List<Link> links = new ArrayList<>();
    private Node start;

    public Chain(String id) {
        this(id, null, null);
    }

    public Chain(String id, String title) {
        this(id, title, null);
    }

    public Chain(String id, String title, String driver) {
        this.id = id;
        this.title = (title == null ? id : title);
        this.driver = (driver == null ? "" : driver);
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
    public String driver() {
        return driver;
    }

    /**
     * 元信息
     */
    public Map<String, Object> meta() {
        return this.meta;
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
    public void addNode(NodeDecl nodeDecl) {
        List<Link> linkAry = new ArrayList<>();

        for (LinkDecl linkSpec : nodeDecl.links) {
            linkAry.add(new Link(this, nodeDecl.id, linkSpec));
        }

        links.addAll(linkAry);

        Node node = new Node(this, nodeDecl, linkAry);
        nodes.put(node.id(), node);
        if (nodeDecl.type == NodeType.start) {
            start = node;
        }
    }

    /**
     * 获取节点
     */
    public Node getNode(String id) {
        return nodes.get(id);
    }

    /**
     * 校验
     */
    public void check() {
        //如果没有配置 start 节点
        if (start == null) {
            //找到没有流入链接的节点，作为开始节点
            for (Node node : nodes.values()) {
                if (Utils.isEmpty(node.prveLinks())) {
                    start = node;
                    break;
                }
            }
        }

        if (start == null) {
            throw new IllegalStateException("No start node found");
        }
    }

    /// ////////

    /**
     * 解析配置文件
     */
    public static Chain parseByUri(String uri) throws IOException {
        URL url = ResourceUtil.findResource(uri, false);
        if (url == null) {
            throw new IllegalArgumentException("Can't find resource: " + uri);
        }

        if (uri.endsWith(".json")) {
            return parseByDom(ONode.load(ResourceUtil.getResourceAsString(url)));
        } else if (uri.endsWith(".yml") || uri.endsWith(".yaml") || uri.endsWith(".properties")) {
            return parseByProperties(Utils.loadProperties(url));
        } else {
            throw new IllegalArgumentException("File format is not supported: " + uri);
        }
    }

    /**
     * 解析配置文本
     *
     * @param text 配置文本（支持 yml, properties, json 格式）
     */
    public static Chain parseByText(String text) {
        return parseByProperties(Utils.buildProperties(text));
    }

    /**
     * 解析配置属性
     *
     * @param properties 配置属性
     */
    public static Chain parseByProperties(Properties properties) {
        return parseByDom(ONode.load(properties));
    }

    /**
     * 解析配置文档模型
     *
     * @param dom 配置文档模型
     */
    public static Chain parseByDom(ONode dom) {
        String id = dom.get("id").getString();
        String title = dom.get("title").getString();
        String driver = dom.get("driver").getString();

        Chain chain = new Chain(id, title, driver);

        //元信息
        Map metaTmp = dom.get("meta").toObject(Map.class);
        if (Utils.isNotEmpty(metaTmp)) {
            chain.meta().putAll(metaTmp);
        }

        //节点（倒序加载，方便自动构建 link）
        List<ONode> layoutTmp = null;
        if (dom.contains("layout")) {
            //新用 layout
            layoutTmp = dom.get("layout").ary();
        } else {
            //弃用 3.1
            layoutTmp = dom.get("nodes").ary();
        }

        NodeDecl nodesLat = null;
        for (int i = layoutTmp.size(); i > 0; i--) {
            ONode n1 = layoutTmp.get(i - 1);

            //自动构建：如果没有时，生成 id
            String n1_id = n1.get("id").getString();
            if (Utils.isEmpty(n1_id)) {
                n1_id = "n-" + i;
            }

            NodeType n1_type = NodeType.nameOf(n1.get("type").getString());

            NodeDecl nodeDecl = new NodeDecl(n1_id, n1_type);

            nodeDecl.title(n1.get("title").getString());
            nodeDecl.meta(n1.get("meta").toObject(Map.class));
            nodeDecl.when(n1.get("when").getString());
            nodeDecl.task(n1.get("task").getString());

            ONode linkNode = n1.get("link");
            if (linkNode.isArray()) {
                //数组模式（多个）
                for (ONode l1 : linkNode.ary()) {
                    if (l1.isObject()) {
                        //对象模式
                        addLink(nodeDecl, l1);
                    } else if (l1.isValue()) {
                        //单值模式
                        nodeDecl.linkAdd(l1.getString());
                    }
                }
            } else if (linkNode.isObject()) {
                //对象模式（单个）
                addLink(nodeDecl, linkNode);
            } else if (linkNode.isValue()) {
                //单值模式（单个）
                nodeDecl.linkAdd(linkNode.getString());
            } else if (linkNode.isNull()) {
                //自动构建：如果没有时，生成 link
                if (nodesLat != null) {
                    nodeDecl.linkAdd(nodesLat.id);
                }
            }

            nodesLat = nodeDecl;
            chain.addNode(nodeDecl);
        }

        //校验结构
        chain.check();

        return chain;
    }

    /**
     * 添加连接
     */
    private static void addLink(NodeDecl nodeDecl, ONode l1) {
        nodeDecl.linkAdd(l1.get("nextId").getString(), ld -> ld
                .title(l1.get("title").getString())
                .meta(l1.get("meta").toObject(Map.class))
                .condition(l1.get("condition").getString()));
    }
}