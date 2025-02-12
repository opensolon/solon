package org.noear.solon.ai.rag.loader;

import org.noear.solon.ai.rag.Document;

import java.util.List;

/**
 * @author noear 2025/2/12 created
 */
public interface DocumentLoader {
    List<Document> load();
}
