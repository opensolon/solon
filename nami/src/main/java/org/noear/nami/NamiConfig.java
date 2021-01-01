package org.noear.nami;

import org.noear.nami.channel.Constants;
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

    public NamiConfig() {
        encoder = Nami.defaultEncoder;
        decoder = Nami.defaultDecoder;
    }

    /**
     * 尝试初始化进行补缺
     * */
    public NamiConfig tryInit() {
        if (encoder == null) {
            setEncoder(FormEncoder.instance);
        }

        if (decoder == null) {
            setDecoder(NamiManager.getDecoder(Constants.ct_json));
        }

        return this;
    }


    //编码器
    private Encoder encoder;
    //解码器
    private Decoder decoder;

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

}
