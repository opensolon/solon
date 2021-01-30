package org.noear.solon.cloud.model;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.utils.LocalUtils;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;

import java.io.Serializable;
import java.util.*;

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
    private String service;

    public String service() {
        return service;
    }

    /**
     * 地址（ip:port）
     */
    private String address;

    public String address() {
        return address;
    }

    public Instance address(String address) {
        this.address = address;
        return this;
    }

    /**
     * 协议（http, ws, tcp...）
     */
    private String protocol;

    public String protocol() {
        return protocol;
    }

    public Instance protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * 权重
     */
    private double weight = 1.0D;

    public double weight() {
        return weight;
    }

    public Instance weight(double weight) {
        this.weight = weight;
        return this;
    }

    /**
     * 元信息
     */
    private Map<String, String> meta;

    public Map<String, String> meta() {
        return meta;
    }

    public Instance metaPut(String name, String value) {
        if (meta == null) {
            meta = new LinkedHashMap<>();
        }

        meta.put(name, value);
        return this;
    }

    public Instance metaPutAll(Map<String, String> map) {
        if (meta == null) {
            meta = new LinkedHashMap<>();
        }

        meta.putAll(map);
        return this;
    }

    public Instance metaRemove(String name) {
        if (meta != null) {
            meta.remove(name);
        }

        return this;
    }

    /**
     * 标签
     */
    private List<String> tags;

    public List<String> tags() {
        return tags;
    }

    public Instance tagsAdd(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }

        tags.add(tag);
        return this;
    }

    public Instance tagsAddAll(Collection<String> list) {
        if (tags == null) {
            tags = new ArrayList<>();
        }

        tags.addAll(list);
        return this;
    }


    public Instance(String service, String address, String protocol) {
        this.service = service;
        this.address = address;
        this.protocol = protocol;
    }


    private static Instance local;

    public static Instance local() {
        if (local == null) {
            local = localNew(new SignalSim(Solon.global().port(), "http", SignalType.HTTP));
        }

        return local;
    }

    public static Instance localNew(Signal signal) {
        Instance instance = new Instance(
                Solon.cfg().appName(),
                LocalUtils.getLocalAddress() + ":" + signal.port(),
                signal.protocol());

        instance.metaPutAll(Solon.cfg().argx());
        instance.metaRemove("server.port");

        instance.tagsAdd("solon");
        if (Utils.isNotEmpty(Solon.cfg().appGroup())) {
            instance.tagsAdd(Solon.cfg().appGroup());
        }

        if (Utils.isNotEmpty(Solon.cfg().appName())) {
            instance.tagsAdd(Solon.cfg().appName());
        }

        return instance;
    }
}