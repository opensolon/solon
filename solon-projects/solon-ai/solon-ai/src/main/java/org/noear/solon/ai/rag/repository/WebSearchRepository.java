package org.noear.solon.ai.rag.repository;

import org.noear.solon.ai.rag.State;
import org.noear.solon.ai.rag.loader.DocumentLoader;

/**
 * @author noear 2025/2/14 created
 */
public class WebSearchRepository implements Repository {
    @Override
    public void load(DocumentLoader loader) {

    }

    @Override
    public State retrieve(String question) {
        return null;
    }
}
