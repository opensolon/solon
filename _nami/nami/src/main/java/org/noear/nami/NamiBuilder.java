package org.noear.nami;

import org.noear.nami.annotation.NamiClient;

import java.lang.reflect.Method;
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
     * @param timeout 超时（单位：秒）
     */
    public NamiBuilder timeout(int timeout) {
        _config.setTimeout(timeout);
        return this;
    }

    /**
     * @param heartbeat 心跳（单为：秒）
     */
    public NamiBuilder heartbeat(int heartbeat) {
        _config.setHeartbeat(heartbeat);
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
     * 设置反序列器
     */
    public NamiBuilder channel(Channel channel) {
        _config.setChannel(channel);
        return this;
    }


    /**
     * 设置负载代理
     */
    public NamiBuilder upstream(Supplier<String> upstream) {
        _config.setUpstream(upstream);
        return this;
    }


    /**
     * 设置服务端地址
     */
    public NamiBuilder url(String url) {
        _config.setUrl(url);
        return this;
    }

    /**
     * 设置服务名字
     */
    public NamiBuilder name(String name) {
        _config.setName(name);
        return this;
    }

    /**
     * 设置服务路径
     */
    public NamiBuilder path(String path) {
        _config.setPath(path);
        return this;
    }

    /**
     * 设置服务分组
     */
    public NamiBuilder group(String group) {
        _config.setGroup(group);
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
     * 设置头
     */
    public NamiBuilder headerSet(String name, String val) {
        _config.setHeader(name, val);
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

        Object instance = Proxy.newProxyInstance(
                clz.getClassLoader(),
                new Class[]{clz},
                handler);

        //::支持接口本身作为Filter的处理
        //@since 1.10
        //@author 夜の孤城
        if (instance instanceof Filter) {
            Filter filterable = (Filter) instance;

            try {
                if (!clz.getMethod("doFilter", Invocation.class).isDefault()) {
                    throw new NamiException("Interface " + clz.getName() + " does not have a default void doFilter(Invocation inv)");
                }
            } catch (NoSuchMethodException e) {
                throw new NamiException(e);
            }

            _config.filterAdd(filterable);
        }

        return instance;
    }
}
