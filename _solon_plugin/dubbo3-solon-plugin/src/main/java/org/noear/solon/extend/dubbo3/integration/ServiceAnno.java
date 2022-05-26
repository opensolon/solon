package org.noear.solon.extend.dubbo3.integration;

import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.noear.solon.Solon;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 1.6
 */
public class ServiceAnno implements Service {
    private Service anno;

    public ServiceAnno(Service anno){
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

    private String path;
    @Override
    public String path() {
        if (path == null) {
            path = Solon.cfg().getByParse(anno.path());
        }

        return path;
    }

    @Override
    public boolean export() {
        return anno.export();
    }


    private String token;
    @Override
    public String token() {
        if (token == null) {
            token = Solon.cfg().getByParse(anno.token());
        }

        return token;
    }

    @Override
    public boolean deprecated() {
        return anno.deprecated();
    }

    @Override
    public boolean dynamic() {
        return anno.dynamic();
    }

    private String accesslog;
    @Override
    public String accesslog() {
        if (accesslog == null) {
            accesslog = Solon.cfg().getByParse(anno.accesslog());
        }

        return accesslog;
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

    private String document;
    @Override
    public String document() {
        if (document == null) {
            document = Solon.cfg().getByParse(anno.document());
        }

        return document;
    }

    @Override
    public int delay() {
        return anno.delay();
    }


    private String local;
    @Override
    public String local() {
        if (local == null) {
            local = Solon.cfg().getByParse(anno.local());
        }

        return local;
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

    private String proxy;
    @Override
    public String proxy() {
        if (proxy == null) {
            proxy = Solon.cfg().getByParse(anno.proxy());
        }

        return proxy;
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

    @Override
    public String layer() {
        return anno.layer();
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

    private String application;
    @Override
    public String application() {
        if (application == null) {
            application = Solon.cfg().getByParse(anno.application());
        }

        return application;
    }

    private String module;
    @Override
    public String module() {
        if (module == null) {
            module = Solon.cfg().getByParse(anno.module());
        }

        return module;
    }

    private String provider;
    @Override
    public String provider() {
        if (provider == null) {
            provider = Solon.cfg().getByParse(anno.provider());
        }

        return provider;
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

    @Override
    public Class<? extends Annotation> annotationType() {
        return anno.annotationType();
    }
}
