package org.noear.nami;

import java.util.Map;

/**
 * 过滤器
 *
 * @author noear
 * @since 1.0
 * */
public interface Filter {
    /**
     * 过滤
     */
    void filter(NamiConfig cfg, String action, String url, Map<String, String> headers, Map<String, Object> args);
}
