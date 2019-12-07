package org.noear.solonclient;

import java.util.Map;

public interface IChannel {
    String call(XProxy proxy, Map<String, String> headers, Map<String, String> args) throws Exception;
}
