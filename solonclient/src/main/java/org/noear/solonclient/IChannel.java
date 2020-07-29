package org.noear.solonclient;

import java.util.Map;

public interface IChannel {
    Result call(XProxy proxy, Map<String, String> headers, Map<String, Object> args) throws Exception;
}
