package org.noear.solonclient;

import java.util.HashMap;
import java.util.Map;

public class XProxyConfig {
    protected String url;
    protected ISerializer serializer;
    protected IChannel channel;
    protected Enctype enctype;

    protected HttpUpstream upstream;
    protected String sev;

    protected Map<String, String> headers = new HashMap<>();
}
