package org.noear.fairy;

import java.util.Map;

public interface IFilter {
    void handle(FairyConfig cfg, String url, Map<String, String> headers, Map<String, Object> args);
}
