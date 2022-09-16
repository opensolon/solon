package org.noear.nami;

import org.noear.nami.common.Constants;

import java.util.*;
import java.util.function.Supplier;

/**
 * Nami - 配置
 *
 * @author noear
 * @since 1.0
 * */
public class Config {
    public Config() {
        encoder = Nami.defaultEncoder;
        decoder = Nami.defaultDecoder;
    }

    /**
     * 尝试初始化进行补缺
     * */
    protected Config init() {
        if (decoder == null) {
            String at = headers.get(Constants.HEADER_ACCEPT);
            if (at != null) {
                decoder = NamiManager.getDecoder(at);
            }

            if (decoder == null) {
                setDecoder(NamiManager.getDecoder(Constants.CONTENT_TYPE_JSON));
            }
        }

        if (encoder == null) {
            String ct = headers.get(Constants.HEADER_CONTENT_TYPE);
            if (ct != null) {
                encoder = NamiManager.getEncoder(ct);
            }
        }

        return this;
    }

    //请求超时设置
    private int timeout;
    //请求心跳频率
    private int heartbeat;
    //编码器
    private Encoder encoder;
    //解码器
    private Decoder decoder;

    private Channel channel;

    //上游
    private Supplier<String> upstream;
    //服务端
    private String url;
    private String name;
    private String path;
    private String group;
    //过滤器
    private Set<Filter> filters = new LinkedHashSet<>();
    //头信息
    private Map<String,String> headers = new LinkedHashMap<>();

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    /**
     * 获取编码器（可以为Null）
     * */
    public Encoder getEncoder() {
        return encoder;
    }
    /**
     * 设置编码器
     * */
    public void setEncoder(Encoder encoder) {
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
    public void setDecoder(Decoder decoder) {
        if (decoder != null) {
            this.decoder = decoder;
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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
    public String getUrl() {
        return url;
    }

    /**
     * 设置uri
     * */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *  获取服务名
     * */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     *  获取服务路径
     * */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    /**
     *  获取服务组
     * */
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * 设置头
     * */
    protected void setHeader(String name, String val){
        headers.put(name,val);
    }

    public String getHeader(String name){
        return headers.get(name);
    }

    public Map<String,String> getHeaders(){
        return Collections.unmodifiableMap(headers);
    }

    /**
     * 获取拦截器
     * */
    public Set<Filter> getFilters() {
        return filters;
    }

    /**
     * 添加拦截器
     * */
    protected void filterAdd(Filter filter) {
        filters.add(filter);
    }

}
