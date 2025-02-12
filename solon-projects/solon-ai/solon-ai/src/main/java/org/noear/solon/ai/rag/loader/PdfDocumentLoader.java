package org.noear.solon.ai.rag.loader;

import org.noear.solon.ai.rag.Document;

import java.util.Collections;
import java.util.List;

/**
 * @author noear 2025/2/12 created
 */
public class PdfDocumentLoader implements DocumentLoader{
    public PdfDocumentLoader(String fileName) {

    }

    @Override
    public List<Document> load() {
        return Collections.emptyList();
    }
}
