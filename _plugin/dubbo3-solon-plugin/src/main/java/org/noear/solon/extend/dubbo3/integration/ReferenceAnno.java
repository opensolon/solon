package org.noear.solon.extend.dubbo3.integration;

import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Reference;
import org.noear.solon.Solon;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 1.6
 */
public class ReferenceAnno implements Reference {
    private Reference anno;
    public ReferenceAnno(Reference anno){
        this.anno = anno;
    }

    @Override
    public Class<?> interfaceClass() {
        return anno.interfaceClass();
    }

    @Override
    public String interfaceName() {
        return anno.interfaceName();
    }


    private String version;
    @Override
    public String version() {
        if (version == null) {
            version = Solon.cfg().getByParse(anno.version());
        }

        return version;
    }

    private String group;
    @Override
    public String group() {
        if (group == null) {
            group = Solon.cfg().getByParse(anno.group());
        }

        return group;
    }

    private String url;
    @Override
    public String url() {
        if (url == null) {
            url = Solon.cfg().getByParse(anno.url());
        }

        return url;
    }

    private String client;
    @Override
    public String client() {
        if (client == null) {
            client = Solon.cfg().getByParse(anno.client());
        }

        return client;
    }

    @Override
    public boolean generic() {
        return anno.generic();
    }

    @Override
    public boolean injvm() {
        return anno.injvm();
    }

    @Override
    public boolean check() {
        return anno.check();
    }

    @Override
    public boolean init() {
        return anno.init();
    }

    @Override
    public boolean lazy() {
        return anno.lazy();
    }

    @Override
    public boolean stubevent() {
        return anno.stubevent();
    }

    private String reconnect;
    @Override
    public String reconnect() {
        if (reconnect == null) {
            reconnect = Solon.cfg().getByParse(anno.reconnect());
        }

        return reconnect;
    }

    @Override
    public boolean sticky() {
        return anno.sticky();
    }

    private String proxy;
    @Override
    public String proxy() {
        if (proxy == null) {
            proxy = Solon.cfg().getByParse(anno.proxy());
        }

        return proxy;
    }

    private String stub;
    @Override
    public String stub() {
        if (stub == null) {
            stub = Solon.cfg().getByParse(anno.stub());
        }

        return stub;
    }

    private String cluster;
    @Override
    public String cluster() {
        if (cluster == null) {
            cluster = Solon.cfg().getByParse(anno.cluster());
        }

        return cluster;
    }

    @Override
    public int connections() {
        return anno.connections();
    }

    @Override
    public int callbacks() {
        return anno.callbacks();
    }


    private String onconnect;
    @Override
    public String onconnect() {
        if (onconnect == null) {
            onconnect = Solon.cfg().getByParse(anno.onconnect());
        }

        return onconnect;
    }

    private String ondisconnect;
    @Override
    public String ondisconnect() {
        if (ondisconnect == null) {
            ondisconnect = Solon.cfg().getByParse(anno.ondisconnect());
        }

        return ondisconnect;
    }

    private String owner;
    @Override
    public String owner() {
        if (owner == null) {
            owner = Solon.cfg().getByParse(anno.owner());
        }

        return owner;
    }

    private String layer;
    @Override
    public String layer() {
        if (layer == null) {
            layer = Solon.cfg().getByParse(anno.layer());
        }

        return layer;
    }

    @Override
    public int retries() {
        return anno.retries();
    }

    private String loadbalance;
    @Override
    public String loadbalance() {
        if (loadbalance == null) {
            loadbalance = Solon.cfg().getByParse(anno.loadbalance());
        }

        return loadbalance;
    }

    @Override
    public boolean async() {
        return anno.async();
    }

    @Override
    public int actives() {
        return anno.actives();
    }

    @Override
    public boolean sent() {
        return anno.sent();
    }

    private String mock;
    @Override
    public String mock() {
        if (mock == null) {
            mock = Solon.cfg().getByParse(anno.mock());
        }

        return mock;
    }

    private String validation;
    @Override
    public String validation() {
        if (validation == null) {
            validation = Solon.cfg().getByParse(anno.validation());
        }

        return validation;
    }

    @Override
    public int timeout() {
        return anno.timeout();
    }

    private String cache;
    @Override
    public String cache() {
        if (cache == null) {
            cache = Solon.cfg().getByParse(anno.cache());
        }

        return cache;
    }

    @Override
    public String[] filter() {
        return anno.filter();
    }

    @Override
    public String[] listener() {
        return anno.listener();
    }

    @Override
    public String[] parameters() {
        return anno.parameters();
    }

    @Override
    public String application() {
        return anno.application();
    }

    private String module;
    @Override
    public String module() {
        if (module == null) {
            module = Solon.cfg().getByParse(anno.module());
        }

        return module;
    }

    private String consumer;
    @Override
    public String consumer() {
        if (consumer == null) {
            consumer = Solon.cfg().getByParse(anno.consumer());
        }

        return consumer;
    }

    private String monitor;
    @Override
    public String monitor() {
        if (monitor == null) {
            monitor = Solon.cfg().getByParse(anno.monitor());
        }

        return monitor;
    }

    @Override
    public String[] registry() {
        return anno.registry();
    }

    private String protocol;
    @Override
    public String protocol() {
        if (protocol == null) {
            protocol = Solon.cfg().getByParse(anno.protocol());
        }

        return protocol;
    }

    private String tag;
    @Override
    public String tag() {
        if (tag == null) {
            tag = Solon.cfg().getByParse(anno.tag());
        }

        return tag;
    }

    @Override
    public Method[] methods() {
        return anno.methods();
    }

    private String id;
    @Override
    public String id() {
        if (id == null) {
            id = Solon.cfg().getByParse(anno.id());
        }

        return id;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return anno.annotationType();
    }
}
