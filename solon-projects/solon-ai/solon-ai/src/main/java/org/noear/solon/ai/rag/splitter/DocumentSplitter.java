package org.noear.solon.ai.rag.splitter;

import org.noear.solon.ai.rag.Document;

import java.util.Arrays;
import java.util.List;

/**
 * 文档分割器
 *
 * @author noear
 * @since 3.1
 */
public interface DocumentSplitter {
    /**
     * 分割
     */
    default List<Document> split(String text) {
        return split(Arrays.asList(new Document(text)));
    }

    /**
     * 分割
     */
    List<Document> split(List<Document> documents);
}
