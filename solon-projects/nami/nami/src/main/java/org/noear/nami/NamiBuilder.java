/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.nami;

import org.noear.eggg.ClassEggg;
import org.noear.eggg.MethodEggg;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.Solon;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.EgggUtil;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

/**
 * Nami 构建器
 *
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
     * 获取负载代理
     * */
    public Supplier<String> upstream() {
        return _config.getUpstream();
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
            throw new NamiException("NamiClient only support interfaces: " + clz.getName());
        }

        if (NativeDetector.isAotRuntime()) {
            //如果是 aot 则注册函数
            if (Solon.app() != null) {
                ClassEggg classEggg = EgggUtil.getClassEggg(clz);
                for (MethodEggg me : classEggg.getPublicMethodEgggs()) {
                    Solon.context().methodWrap(classEggg, me);
                }
            }
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
                    throw new NamiException("There is no default doFilter(Invocation inv): " + clz.getName());
                }
            } catch (NoSuchMethodException e) {
                throw new NamiException(e);
            }

            _config.filterAdd(filterable);
        }

        return instance;
    }
}
