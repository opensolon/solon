package org.noear.solon.ai.rag.loader;

import org.noear.solon.ai.rag.DocumentLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 虚拟文档加载器
 *
 * @author noear
 * @since 3.1
 */
public abstract class AbstractDocumentLoader implements DocumentLoader {
    protected final Map<String, Object> additionalMetadata = new HashMap<>();

    @Override
    public DocumentLoader additionalMetadata(String key, Object value) {
        this.additionalMetadata.put(key, value);
        return this;
    }

    @Override
    public DocumentLoader additionalMetadata(Map<String, Object> metadata) {
        this.additionalMetadata.putAll(metadata);
        return this;
    }
}
