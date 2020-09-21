package org.noear.solonclient;

import java.util.Map;

public interface IInterceptor {
    void execute(XProxy proxy, Map<String, String> header, Map args);
}
