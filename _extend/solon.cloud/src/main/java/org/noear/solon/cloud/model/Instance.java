package org.noear.solon.cloud.model;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.utils.LocalUtils;

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
public class Instance implements Serializable {
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


    private static Instance local;

    public static Instance local() {
        if (local == null) {
            local = localNew();
        }

        return local;
    }

    public static Instance localNew() {
        Instance instance = new Instance();

        instance.address = LocalUtils.getLocalAddress() + ":" + Solon.global().port();
        instance.service = Solon.cfg().appName();
        instance.protocol = "http";

        instance.meta = new HashMap<>(Solon.cfg().argx());
        instance.meta.remove("server.port");

        instance.tags = new ArrayList<>();
        instance.tags.add("solon");
        if (Utils.isNotEmpty(Solon.cfg().appGroup())) {
            instance.tags.add(Solon.cfg().appGroup());
        }
        if (Utils.isNotEmpty(Solon.cfg().appName())) {
            instance.tags.add(Solon.cfg().appName());
        }

        return instance;
    }
}
