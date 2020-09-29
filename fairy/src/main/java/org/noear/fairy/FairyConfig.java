package org.noear.fairy;

import org.noear.fairy.channel.HttpChannel;
import org.noear.fairy.decoder.FastjsonDecoder;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.FormEncoder;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fairy - 配置
 *
 * @author noear
 * @since 1.0
 * */
public class FairyConfig {
    private static boolean HAS_SNACK3 = Utils.hasClass("org.noear.snack.ONode");
    private static boolean HAS_FASTJSON = Utils.hasClass("com.alibaba.fastjson.JSONObject");

    public FairyConfig(){
        encoder = Fairy.defaultEncoder;
        decoder = Fairy.defaultDecoder;
        channel = Fairy.defaultChannel;
    }

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
            setChannel(HttpChannel.instance);
        }

        return this;
    }


    //编码器
    private IEncoder encoder;
    //解码器
    private IDecoder decoder;
    //通道
    private IChannel channel;

    private Upstream upstream;

    private String server;

    private Set<IFilter> filters = new LinkedHashSet<>();


    public IEncoder getEncoder() {
        return encoder;
    }

    protected void setEncoder(IEncoder encoder) {
        if(encoder != null){
            this.encoder = encoder;
        }
    }

    public IDecoder getDecoder() {
        return decoder;
    }

    protected void setDecoder(IDecoder decoder) {
        if(decoder != null){
           this.decoder = decoder;
        }
    }


    public IChannel getChannel() {
        return channel;
    }

    protected void setChannel(IChannel channel) {
        if(channel != null){
            this.channel = channel;
        }
    }


    public Upstream getUpstream() {
        return upstream;
    }

    protected void setUpstream(Upstream upstream) {
        this.upstream = upstream;
    }


    public String getServer() {
        return server;
    }

    protected void setServer(String server) {
        this.server = server;
    }


    public Set<IFilter> getFilters() {
        return filters;
    }

    protected void filterAdd(IFilter filter){
        filters.add(filter);
    }
}
