package org.noear.solonclient;

import org.noear.solonclient.channel.HttpChannel;
import org.noear.solonclient.serializer.FastjsonSerializerD;
import org.noear.solonclient.serializer.FormSerializer;

import java.util.HashMap;
import java.util.Map;

public class XProxyConfig {
    private ISerializer serializer;
    private IDeserializer deserializer;

    private IChannel channel;

    private XUpstream upstream;
    private String server;

    private Map<String, String> headers = new HashMap<>();

    public ISerializer getSerializer() {
        return serializer;
    }

    protected void setSerializer(ISerializer serializer) {
        if(serializer == null){
            this.serializer = FormSerializer.instance;
        }else {
            this.serializer = serializer;
        }
    }

    public IDeserializer getDeserializer() {
        return deserializer;
    }

    protected void setDeserializer(IDeserializer deserializer) {
        if(deserializer == null){
            this.deserializer = FastjsonSerializerD.instance;
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

    public XUpstream getUpstream() {
        return upstream;
    }

    protected void setUpstream(XUpstream upstream) {
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
