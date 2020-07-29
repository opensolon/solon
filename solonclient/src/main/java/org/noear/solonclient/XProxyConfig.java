package org.noear.solonclient;

import org.noear.solonclient.channel.HttpChannel;
import org.noear.solonclient.serializer.FastjsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class XProxyConfig {
    private ISerializer serializer;
    private IDeserializer deserializer;

    private IChannel channel;
    private Enctype enctype;

    private HttpUpstream upstream;
    private String server;

    private Map<String, String> headers = new HashMap<>();

    public ISerializer getSerializer() {
        return serializer;
    }

    protected void setSerializer(ISerializer serializer) {
        if(serializer == null){
            this.serializer = FastjsonSerializer.instance;
        }else {
            this.serializer = serializer;
        }
    }

    public IDeserializer getDeserializer() {
        return deserializer;
    }

    protected void setDeserializer(IDeserializer deserializer) {
        if(deserializer == null){
            this.deserializer = FastjsonSerializer.instance;
        }else {
            this.deserializer = deserializer;
        }
    }

    public IChannel getChannel() {
        return channel;
    }

    protected void setChannel(IChannel channel) {
        if(channel == null){
            this.channel = HttpChannel.instance;
        }else {
            this.channel = channel;
        }
    }

    public Enctype getEnctype() {
        return enctype;
    }

    protected void setEnctype(Enctype enctype) {
        this.enctype = enctype;
    }

    public HttpUpstream getUpstream() {
        return upstream;
    }

    protected void setUpstream(HttpUpstream upstream) {
        this.upstream = upstream;
    }

    public String getServer() {
        return server;
    }

    protected void setServer(String server) {
        this.server = server;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    protected void headerAdd(String name, String value){
        headers.put(name,value);
    }
}
