package org.noear.solonclient;

import java.util.Map;

public interface IFilter {
    void handle(XProxy proxy, Map<String, String> header, Map args);
}
