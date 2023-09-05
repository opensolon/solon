package com.dtflys.forest.solon.properties;

import com.dtflys.forest.converter.ForestConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.11
 */
public class ForestConverterItemProperties {

    private Class<? extends ForestConverter> type;

    private Map<String, Object> parameters = new HashMap<>();

    public Class<? extends ForestConverter> getType() {
        return type;
    }

    public void setType(Class<? extends ForestConverter> type) {
        this.type = type;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
