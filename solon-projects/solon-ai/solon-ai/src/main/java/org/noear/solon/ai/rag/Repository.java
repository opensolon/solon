package org.noear.solon.ai.rag;

import org.noear.solon.ai.rag.loader.DocumentLoader;

/**
 * 知识库（向量存储与索引）
 *
 * @author noear
 * @since 3.1
 */
public interface Repository {
    /**
     * 加载
     */
    void load(DocumentLoader loader);

    /**
     * 检索
     */
    State retrieve(String question);
}