package org.noear.solon.extend.shiro.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class ShiroFilterChainDefinitionDefault implements ShiroFilterChainDefinition {
    private final Map<String, String> filterChainDefinitionMap = new LinkedHashMap();

    public ShiroFilterChainDefinitionDefault() {
    }

    public void addPathDefinition(String antPath, String definition) {
        this.filterChainDefinitionMap.put(antPath, definition);
    }

    public void addPathDefinitions(Map<String, String> pathDefinitions) {
        this.filterChainDefinitionMap.putAll(pathDefinitions);
    }

    public Map<String, String> getFilterChainMap() {
        return this.filterChainDefinitionMap;
    }
}