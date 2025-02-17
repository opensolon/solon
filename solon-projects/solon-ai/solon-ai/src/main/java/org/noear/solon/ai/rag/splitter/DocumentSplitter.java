package org.noear.solon.ai.rag.splitter;

import org.noear.solon.ai.rag.Document;

import java.util.List;

/**
 * 文档切割器
 *
 * @author noear
 * @since 3.1
 */
public interface DocumentSplitter {
    List<Document> split(List<Document> documents);
}
