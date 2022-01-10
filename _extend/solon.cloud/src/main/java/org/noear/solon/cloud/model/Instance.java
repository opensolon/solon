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

    private String service;

    /**
     * 服务名；实例化后不能修改
     */
    public String service() {
        return service;
    }


    private String address;

    /**
     * 地址；实例化后不能修改（ip:port）
     */
    public String address() {
        return address;
    }

    /**
     * 服务和地址(service@ip:port)
     * */
    private String serviceAndAddress;
    public String serviceAndAddress() {
        if (serviceAndAddress == null) {
            serviceAndAddress = service() + "@" + address();
        }

        return serviceAndAddress;
    }

    private String protocol;

    /**
     * 协议（http, ws, tcp...）
     */
    public String protocol() {
        return protocol;
    }

    public Instance protocol(String protocol) {
        if (Utils.isNotEmpty(protocol)) {
            this.protocol = protocol;
            _uri = null;
        }

        return this;
    }

    private transient String _uri;
    public String uri() {
        if (_uri == null) {
            if (Utils.isEmpty(protocol)) {
                _uri = "http://" + address;
            } else {
                _uri = protocol + "://" + address;
            }
        }

        return _uri;
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
    private Map<String, String> meta = new HashMap<>();

    public Map<String, String> meta() {
        return meta;
    }

    public Instance metaPut(String name, String value) {
        if (value != null) {
            meta.put(name, value);
        }

        return this;
    }

    public String metaGet(String name) {
        return meta.get(name);
    }

    public Instance metaPutAll(Map<String, String> map) {
        if (map != null) {
            meta.putAll(map);

            protocol(map.get("protocol"));
        }

        return this;
    }

    public Instance metaRemove(String name) {
        meta.remove(name);

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


    /**
     * 用于序列化
     * */
    public Instance(){

    }
    public Instance(String service, String address) {
        if (Utils.isEmpty(service)) {
            this.service = Solon.cfg().appName();
        } else {
            this.service = service;
        }

        this.address = address;
    }


    private static Instance local;

    public static Instance local() {
        if (local == null) {
            local = localNew(new SignalSim(Solon.cfg().appName(), Solon.global().port(), "http", SignalType.HTTP));
        }

        return local;
    }

    public static Instance localNew(Signal signal) {
        Instance n1 = new Instance(
                signal.name(),
                LocalUtils.getLocalAddress() + ":" + signal.port());

        n1.protocol(signal.protocol());

        n1.metaPutAll(Solon.cfg().argx());
        n1.metaRemove("server.port"); //移除端口元信息
        n1.metaPut("protocol", signal.protocol());

        n1.tagsAdd("solon");
        if (Utils.isNotEmpty(Solon.cfg().appGroup())) {
            n1.tagsAdd(Solon.cfg().appGroup());
        }

        if (Utils.isNotEmpty(Solon.cfg().appName())) {
            n1.tagsAdd(Solon.cfg().appName());
        }

        return n1;
    }
}