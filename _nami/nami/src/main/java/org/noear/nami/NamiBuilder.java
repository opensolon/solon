package org.noear.nami;

import org.noear.nami.annotation.NamiClient;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

/**
 * Nami 构建器
 * @author noear
 * @since 1.0
 */
public class NamiBuilder {
    private final Config _config;

    protected NamiBuilder() {
        _config = new Config();
    }

    protected NamiBuilder(Config config) {
        _config = config;
    }

    /**
     * 设置负载代理
     */
    public NamiBuilder upstream(Supplier<String> upstream) {
        _config.setUpstream(upstream);
        return this;
    }

    /**
     * 设置序列化器
     */
    public NamiBuilder encoder(Encoder encoder) {
        _config.setEncoder(encoder);
        return this;
    }

    /**
     * 设置反序列器
     */
    public NamiBuilder decoder(Decoder decoder) {
        _config.setDecoder(decoder);
        return this;
    }

    /**
     * 设置头
     * */
    public NamiBuilder headerSet(String name, String val) {
        _config.setHeader(name, val);
        return this;
    }

    /**
     * 设置反序列器
     */
    public NamiBuilder channel(Channel channel) {
        _config.setChannel(channel);
        return this;
    }

    /**
     * 添加拦截器
     */
    public NamiBuilder filterAdd(Filter filter) {
        _config.filterAdd(filter);
        return this;
    }


    /**
     * 设置服务端
     */
    public NamiBuilder url(String url) {
        _config.setUrl(url);
        return this;
    }

    public NamiBuilder name(String name) {
        _config.setName(name);
        return this;
    }

    public NamiBuilder path(String path) {
        _config.setPath(path);
        return this;
    }

    public NamiBuilder group(String group) {
        _config.setGroup(group);
        return this;
    }

    public Nami build() {
        return new Nami(_config);
    }

    /**
     * 创建接口代理
     */
    public <T> T create(Class<?> clz) {
        NamiClient client = clz.getAnnotation(NamiClient.class);

        return (T) create(clz, client);
    }

    /**
     * 创建接口代理
     */
    public Object create(Class<?> clz, NamiClient client) {
        if (clz == null) {
            return null;
        }

        if (clz.isInterface() == false) {
            throw new NamiException("NamiClient only support interfaces");
        }

        NamiHandler handler = new NamiHandler(clz, _config, client);

        return Proxy.newProxyInstance(
                clz.getClassLoader(),
                new Class[]{clz},
                handler);
    }
}
