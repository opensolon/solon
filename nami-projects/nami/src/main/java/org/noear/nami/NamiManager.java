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

import org.noear.solon.core.util.ClassUtil;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Nami - 扩展管理器
 *
 * @author noear
 * @since 1.2
 * @since 2.4
 */
public class NamiManager {
    static final Map<String, Decoder> decoderMap = new HashMap<>();
    static final Map<String, Encoder> encoderMap = new HashMap<>();
    static final Map<String, Channel> channelMap = new HashMap<>();
    static final Map<Class<?>, NamiConfiguration> configuratorMap = new ConcurrentHashMap<>();

    static Decoder decoderFirst;
    static Encoder encoderFirst;

    /**
     * 全局拦截器
     */
    static final Set<Filter> filterSet = new LinkedHashSet<>();

    /**
     * 登记解码器
     */
    public static void reg(Decoder decoder) {
        decoderMap.put(decoder.enctype(), decoder);
        if (decoderFirst == null) {
            decoderFirst = decoder;
        }
    }

    /**
     * 登记解码器
     */
    public static void regIfAbsent(Decoder decoder) {
        decoderMap.putIfAbsent(decoder.enctype(), decoder);
        if (decoderFirst == null) {
            decoderFirst = decoder;
        }
    }

    /**
     * 登记编码器
     */
    public static void reg(Encoder encoder) {
        encoderMap.put(encoder.enctype(), encoder);
        if(encoderFirst == null){
            encoderFirst = encoder;
        }
    }

    /**
     * 登记编码器
     */
    public static void regIfAbsent(Encoder encoder) {
        encoderMap.putIfAbsent(encoder.enctype(), encoder);
        if(encoderFirst == null){
            encoderFirst = encoder;
        }
    }


    /**
     * 登记拦截器
     */
    public static void reg(Filter filter) {
        filterSet.add(filter);
    }


    /**
     * 登记通道
     */
    public static void reg(String scheme, Channel channel) {
        channelMap.put(scheme, channel);
    }

    /**
     * 登记通道
     */
    public static void regIfAbsent(String scheme, Channel channel) {
        channelMap.putIfAbsent(scheme, channel);
    }

    /**
     * 获取解码器
     */
    public static Decoder getDecoder(String enctype) {
        if(enctype == null){
            return null;
        }

        return decoderMap.get(enctype);
    }

    /**
     * 获取第一个解码器
     * @since 2.4
     */
    public static Decoder getDecoderFirst() {
        return decoderFirst;
    }

    /**
     * 获取编码器
     */
    public static Encoder getEncoder(String enctype) {
        if(enctype == null){
            return null;
        }

        return encoderMap.get(enctype);
    }

    /**
     * 获取第一个编码器
     * @since 2.4
     */
    public static Encoder getEncoderFirst() {
        return encoderFirst;
    }

    /**
     * 获取过滤器
     */
    public static Set<Filter> getFilters() {
        return filterSet;
    }

    /**
     * 获取通道
     */
    public static Channel getChannel(String scheme) {
        return channelMap.get(scheme);
    }

    /**
     * 获取配置器
     */
    public static NamiConfiguration getConfigurator(Class<? extends NamiConfiguration> clz) throws Exception {
        NamiConfiguration tmp = configuratorMap.computeIfAbsent(clz, k->{
            return ClassUtil.tryInstance(clz, null);
        });

        return tmp;
    }
}
