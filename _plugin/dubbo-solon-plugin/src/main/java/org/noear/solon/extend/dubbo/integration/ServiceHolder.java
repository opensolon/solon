package org.noear.solon.extend.dubbo.integration;

import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 1.6
 */
public class ServiceHolder implements Service {
    private Service anno;

    public ServiceHolder(Service anno){
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
    public String path() {
        return anno.path();
    }

    @Override
    public boolean export() {
        return anno.export();
    }

    @Override
    public String token() {
        return anno.token();
    }

    @Override
    public boolean deprecated() {
        return anno.deprecated();
    }

    @Override
    public boolean dynamic() {
        return anno.dynamic();
    }

    @Override
    public String accesslog() {
        return anno.accesslog();
    }

    @Override
    public int executes() {
        return anno.executes();
    }

    @Override
    public boolean register() {
        return anno.register();
    }

    @Override
    public int weight() {
        return anno.weight();
    }

    @Override
    public String document() {
        return anno.document();
    }

    @Override
    public int delay() {
        return anno.delay();
    }

    @Override
    public String local() {
        return anno.local();
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
    public String proxy() {
        return anno.proxy();
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
    public String provider() {
        return anno.provider();
    }

    @Override
    public String[] protocol() {
        return anno.protocol();
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
    public String tag() {
        return anno.tag();
    }

    @Override
    public Method[] methods() {
        return anno.methods();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return anno.annotationType();
    }
}
