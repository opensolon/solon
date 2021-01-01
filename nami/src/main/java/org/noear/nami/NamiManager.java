package org.noear.nami;

import org.noear.nami.channel.http.HttpChannel;
import org.noear.nami.decoder.FastjsonDecoder;
import org.noear.nami.decoder.HessionDecoder;
import org.noear.nami.encoder.FastjsonEncoder;
import org.noear.nami.encoder.HessionEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理器
 *
 * @author noear 2021/1/1 created
 * @since 1.2
 */
public class NamiManager {
    static final Map<String, Decoder> decoderMap = new HashMap<>();
    static final Map<String, Encoder> encoderMap = new HashMap<>();
    static final Map<String, NamiChannel> channelMap = new HashMap<>();

    /**
     * 登记解码器
     */
    public static void reg(Decoder decoder) {
        decoderMap.put(decoder.enctype().contentType, decoder);
    }

    public static void regIfAbsent(Decoder decoder) {
        decoderMap.putIfAbsent(decoder.enctype().contentType, decoder);
    }

    /**
     * 登记编码器
     */
    public static void reg(Encoder encoder) {
        encoderMap.put(encoder.enctype().contentType, encoder);
    }

    /**
     * 登记编码器
     */
    public static void regIfAbsent(Encoder encoder) {
        encoderMap.putIfAbsent(encoder.enctype().contentType, encoder);
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

    public static Decoder getDecoder(String contentType) {
        return decoderMap.get(contentType);
    }

    public static Encoder getEncoder(String contentType) {
        return encoderMap.get(contentType);
    }

    public static NamiChannel getChannel(String scheme) {
        return channelMap.get(scheme);
    }


    static {
        if (NamiConfig.HAN_HESSIAN) {
            NamiManager.regIfAbsent(HessionDecoder.instance);
            NamiManager.regIfAbsent(HessionEncoder.instance);
        }

        if (NamiConfig.HAS_FASTJSON) {
            NamiManager.regIfAbsent(FastjsonDecoder.instance);
            NamiManager.regIfAbsent(FastjsonEncoder.instance);
        }

        if (NamiConfig.HAS_SNACK3) {
            NamiManager.regIfAbsent(FastjsonDecoder.instance);
            NamiManager.regIfAbsent(FastjsonEncoder.instance);
        }

        NamiManager.regIfAbsent("http", HttpChannel.instance);
        NamiManager.regIfAbsent("https", HttpChannel.instance);
    }
}
