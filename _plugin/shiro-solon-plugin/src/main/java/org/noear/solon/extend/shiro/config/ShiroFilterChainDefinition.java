package org.noear.solon.extend.shiro.config;

import java.util.Map;


/**
 * @author noear
 * @since 1.3
 */
public interface ShiroFilterChainDefinition {
    Map<String, String> getFilterChainMap();
}
