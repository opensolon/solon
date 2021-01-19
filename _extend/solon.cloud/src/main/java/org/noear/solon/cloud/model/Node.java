package org.noear.solon.cloud.model;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.utils.LocalUtils;

import javax.rmi.CORBA.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务节点模型
 *
 * @author noear
 * @since 1.2
 */
public class Node implements Serializable {
    /**
     * 服务名
     */
    public String service;

    /**
     * 地址（ip:port）
     */
    public String address;

    /**
     * 协议（http, ws, tcp...）
     */
    public String protocol;

    /**
     * 权重
     */
    public double weight = 1.0D;

    /**
     * 元信息
     */
    public Map<String, String> meta;

    /**
     * 标签
     */
    public List<String> tags;


    private static Node local;

    public static Node local() {
        if (local == null) {
            local = localNew();
        }

        return local;
    }

    public static Node localNew() {
        Node node = new Node();

        node.address = LocalUtils.getLocalAddress() + ":" + Solon.global().port();
        node.service = Solon.cfg().appName();
        node.protocol = "http";

        node.meta = new HashMap<>(Solon.cfg().argx());
        node.meta.remove("server.port");

        node.tags = new ArrayList<>();
        node.tags.add("solon");
        if (Utils.isNotEmpty(Solon.cfg().appGroup())) {
            node.tags.add(Solon.cfg().appGroup());
        }
        if (Utils.isNotEmpty(Solon.cfg().appName())) {
            node.tags.add(Solon.cfg().appName());
        }

        return node;
    }
}
