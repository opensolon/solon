package org.noear.fairy;

import java.util.Map;

public interface IChannel {
    Result call(FairyConfig cfg, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable;
}
