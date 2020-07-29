package org.noear.solonclient;

import java.util.Map;

public interface IChannel {
    Result call(XProxyConfig cfg, String url, Map<String, String> headers, Map<String, Object> args) throws Exception;
}
