package org.noear.solon.ai.embedding;


import org.noear.solon.ai.chat.ChatOptions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2025/2/17 created
 */
public class EmbeddingOptions {


    public static EmbeddingOptions of() {
        return new EmbeddingOptions();
    }


    private Map<String, Object> options = new LinkedHashMap<>();

    /**
     * 所有选项
     */
    public Map<String, Object> options() {
        return options;
    }

    /**
     * 选项获取
     */
    public Object option(String key) {
        return options.get(key);
    }

    /**
     * 选项添加
     */
    public EmbeddingOptions optionAdd(String key, Object val) {
        options.put(key, val);
        return this;
    }

    public EmbeddingOptions dimensions(int dimensions) {
        return optionAdd("dimensions", dimensions);
    }


    public EmbeddingOptions user(String user) {
        return optionAdd("user", user);
    }
}
