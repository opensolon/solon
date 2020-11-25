package org.noear.fairy;

import org.noear.fairy.channel.OkHttpChannel;
import org.noear.fairy.decoder.FastjsonDecoder;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.FormEncoder;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Fairy - 配置
 *
 * @author noear
 * @since 1.0
 * */
public class FairyConfig {
    private static boolean HAS_SNACK3 = hasClass("org.noear.snack.ONode");
    private static boolean HAS_FASTJSON = hasClass("com.alibaba.fastjson.JSONObject");

    public FairyConfig() {
        encoder = Fairy.defaultEncoder;
        decoder = Fairy.defaultDecoder;
        channel = Fairy.defaultChannel;
    }

    /**
     * 尝试初始化进行补缺
     * */
    public FairyConfig tryInit() {
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
    private IEncoder encoder;
    //解码器
    private IDecoder decoder;
    //通道
    private IChannel channel;
    //上游
    private Supplier<String> upstream;
    //服务端
    private String uri;
    //过滤器
    private Set<IFilter> filters = new LinkedHashSet<>();

    /**
     * 获取编码器
     * */
    public IEncoder getEncoder() {
        return encoder;
    }
    /**
     * 设置编码器
     * */
    protected void setEncoder(IEncoder encoder) {
        if (encoder != null) {
            this.encoder = encoder;
        }
    }

    /**
     * 获取解码器
     * */
    public IDecoder getDecoder() {
        return decoder;
    }
    /**
     * 设置解码器
     * */
    protected void setDecoder(IDecoder decoder) {
        if (decoder != null) {
            this.decoder = decoder;
        }
    }

    /**
     * 获取通道
     * */
    public IChannel getChannel() {
        return channel;
    }
    /**
     * 设置通道
     * */
    protected void setChannel(IChannel channel) {
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
    public Set<IFilter> getFilters() {
        return filters;
    }
    /**
     * 添加过滤器
     * */
    protected void filterAdd(IFilter filter) {
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
