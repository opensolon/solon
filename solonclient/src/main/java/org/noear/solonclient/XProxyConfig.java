package org.noear.solonclient;

import java.util.HashMap;
import java.util.Map;

public class XProxyConfig {
    public ISerializer serializer;
    public IDeserializer deserializer;

    public IChannel channel;
    public Enctype enctype;

    public HttpUpstream upstream;
    public String sev;

    public Map<String, String> headers = new HashMap<>();
}
