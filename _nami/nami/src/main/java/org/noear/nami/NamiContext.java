package org.noear.nami;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.4
 */
public class NamiContext {
    public final NamiConfig config;
    public final Method method;
    public final String action;

    public final Map<String, String> headers = new LinkedHashMap<>();
    public final Map<String, Object> args = new LinkedHashMap<>();
    public Object body;
    public String url;

    public NamiContext(NamiConfig config, Method method, String action) {
        this.config = config;
        this.method = method;
        this.action = action;
    }
}
