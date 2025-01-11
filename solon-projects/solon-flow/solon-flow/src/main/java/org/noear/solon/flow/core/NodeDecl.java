package org.noear.solon.flow.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/1/11 created
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
