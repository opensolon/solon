package org.noear.nami;

import org.noear.nami.channel.OkHttpChannel;
import org.noear.nami.decoder.FastjsonDecoder;
import org.noear.nami.decoder.SnackDecoder;
import org.noear.nami.encoder.FormEncoder;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Fairy - 配置
 *
 * @author noear
 * @since 1.0
 * */
public class NamiConfig {
    private static boolean HAS_SNACK3 = hasClass("org.noear.snack.ONode");
    private static boolean HAS_FASTJSON = hasClass("com.alibaba.fastjson.JSONObject");

    public NamiConfig() {
        encoder = Nami.defaultEncoder;
        decoder = Nami.defaultDecoder;
        channel = Nami.defaultChannel;
    }

    /**
     * 尝试初始化进行补缺
     * */
    public NamiConfig tryInit() {
        if (encoder == null) {
            setEncoder(FormEncoder.instance);
        }

        if (decoder == null) {
            if (HAS_FASTJSON) {
                setDecoder(FastjsonDecoder.instance);
            } else if (HAS_SNACK3) {
                setDecoder(SnackDecoder.instance);
            }
        }
        if (channel == null) {
            setChannel(OkHttpChannel.instance);
        }

        return this;
    }


    //编码器
    private Encoder encoder;
    //解码器
    private Decoder decoder;
    //通道
    private NamiChannel channel;
    //上游
    private Supplier<String> upstream;
    //服务端
    private String uri;
    //过滤器
    private Set<Filter> filters = new LinkedHashSet<>();

    /**
     * 获取编码器
     * */
    public Encoder getEncoder() {
        return encoder;
    }
    /**
     * 设置编码器
     * */
    protected void setEncoder(Encoder encoder) {
        if (encoder != null) {
            this.encoder = encoder;
        }
    }

    /**
     * 获取解码器
     * */
    public Decoder getDecoder() {
        return decoder;
    }
    /**
     * 设置解码器
     * */
    protected void setDecoder(Decoder decoder) {
        if (decoder != null) {
            this.decoder = decoder;
        }
    }

    /**
     * 获取通道
     * */
    public NamiChannel getChannel() {
        return channel;
    }
    /**
     * 设置通道
     * */
    protected void setChannel(NamiChannel channel) {
        if (channel != null) {
            this.channel = channel;
        }
    }

    /**
     * 获取上游
     * */
    public Supplier<String> getUpstream() {
        return upstream;
    }
    /**
     * 设置上游
     * */
    protected void setUpstream(Supplier<String> upstream) {
        this.upstream = upstream;
    }

    /**
     * 获取uri
     * */
    public String getUri() {
        return uri;
    }
    /**
     * 设置uri
     * */
    protected void setUri(String uri) {
        this.uri = uri;
    }


    /**
     * 获取过滤器
     * */
    public Set<Filter> getFilters() {
        return filters;
    }
    /**
     * 添加过滤器
     * */
    protected void filterAdd(Filter filter) {
        filters.add(filter);
    }


    //检查类是否存在
    //
    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }
}
