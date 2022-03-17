package org.noear.solon.extend.dubbo.integration;

import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Reference;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 1.6
 */
public class ReferenceHolder implements Reference {
    private Reference anno;
    public ReferenceHolder(Reference anno){
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

    @Override
    public String version() {
        return anno.version();
    }

    @Override
    public String group() {
        return anno.group();
    }

    @Override
    public String url() {
        return anno.url();
    }

    @Override
    public String client() {
        return anno.client();
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

    @Override
    public String reconnect() {
        return anno.reconnect();
    }

    @Override
    public boolean sticky() {
        return anno.sticky();
    }

    @Override
    public String proxy() {
        return anno.proxy();
    }

    @Override
    public String stub() {
        return anno.stub();
    }

    @Override
    public String cluster() {
        return anno.cluster();
    }

    @Override
    public int connections() {
        return anno.connections();
    }

    @Override
    public int callbacks() {
        return anno.callbacks();
    }

    @Override
    public String onconnect() {
        return anno.onconnect();
    }

    @Override
    public String ondisconnect() {
        return anno.ondisconnect();
    }

    @Override
    public String owner() {
        return anno.owner();
    }

    @Override
    public String layer() {
        return anno.layer();
    }

    @Override
    public int retries() {
        return anno.retries();
    }

    @Override
    public String loadbalance() {
        return anno.loadbalance();
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

    @Override
    public String mock() {
        return anno.mock();
    }

    @Override
    public String validation() {
        return anno.validation();
    }

    @Override
    public int timeout() {
        return anno.timeout();
    }

    @Override
    public String cache() {
        return anno.cache();
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

    @Override
    public String module() {
        return anno.module();
    }

    @Override
    public String consumer() {
        return anno.consumer();
    }

    @Override
    public String monitor() {
        return anno.monitor();
    }

    @Override
    public String[] registry() {
        return anno.registry();
    }

    @Override
    public String protocol() {
        return anno.protocol();
    }

    @Override
    public String tag() {
        return anno.tag();
    }

    @Override
    public Method[] methods() {
        return anno.methods();
    }

    @Override
    public String id() {
        return anno.id();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return anno.annotationType();
    }
}
