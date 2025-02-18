package org.noear.solon.ai.rag.loader;

import org.noear.solon.ai.rag.Document;

import java.util.List;

/**
 * 文档加载器
 *
 * @author noear
 * @since 3.1
 */
public interface DocumentLoader {
    /**
     * 加载文档
     */
    List<Document> load();
}
