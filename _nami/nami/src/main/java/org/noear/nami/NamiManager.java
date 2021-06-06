package org.noear.nami;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Nami - 扩展管理器
 *
 * @author noear
 * @since 1.2
 */
public class NamiManager {
    static final Map<String, Decoder> decoderMap = new HashMap<>();
    static final Map<String, Encoder> encoderMap = new HashMap<>();
    static final Map<String, NamiChannel> channelMap = new HashMap<>();
    static final Map<Class<?>, NamiConfiguration> configuratorMap = new HashMap<>();
    static final Set<Interceptor> interceptorSet = new LinkedHashSet<>();

    /**
     * 登记解码器
     */
    public static void reg(Decoder decoder) {
        decoderMap.put(decoder.enctype(), decoder);
    }

    /**
     * 登记解码器
     * */
    public static void regIfAbsent(Decoder decoder) {
        decoderMap.putIfAbsent(decoder.enctype(), decoder);
    }

    /**
     * 登记编码器
     */
    public static void reg(Encoder encoder) {
        encoderMap.put(encoder.enctype(), encoder);
    }

    /**
     * 登记编码器
     */
    public static void regIfAbsent(Encoder encoder) {
        encoderMap.putIfAbsent(encoder.enctype(), encoder);
    }


    /**
     * 登记拦截器
     */
    public static void reg(Interceptor interceptor) {
        interceptorSet.add(interceptor);
    }


    /**
     * 登记通道
     */
    public static void reg(String scheme, NamiChannel namiChannel) {
        channelMap.put(scheme, namiChannel);
    }

    /**
     * 登记通道
     */
    public static void regIfAbsent(String scheme, NamiChannel namiChannel) {
        channelMap.putIfAbsent(scheme, namiChannel);
    }

    public static Decoder getDecoder(String enctype) {
        return decoderMap.get(enctype);
    }

    public static Encoder getEncoder(String enctype) {
        return encoderMap.get(enctype);
    }

    public static Set<Interceptor> getInterceptorSet() {
        return interceptorSet;
    }

    public static NamiChannel getChannel(String scheme) {
        return channelMap.get(scheme);
    }

    public static NamiConfiguration getConfigurator(Class<? extends NamiConfiguration> clz) throws Exception {
        NamiConfiguration tmp = configuratorMap.get(clz);

        if (tmp == null) {
            synchronized (clz) {
                tmp = configuratorMap.get(clz);
                if (tmp == null) {
                    tmp = clz.newInstance();
                    configuratorMap.put(clz, tmp);
                }
            }
        }

        return tmp;
    }
}
