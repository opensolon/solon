package org.noear.solon.ai.rag.repository;

import org.noear.solon.ai.rag.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear
 * @since 3.1
 */
public class SimpleRepository implements Repository {
    private Map<String, Document> store = new ConcurrentHashMap<>();

    @Override
    public void put(List<Document> documents) {
        for (Document doc : documents) {
            store.put(doc.getId(), doc);
        }
    }

    @Override
    public void remove(String id) {
        store.remove(id);
    }

    @Override
    public List<Document> search(String message) {
        List<Document> tmp = new ArrayList<>();
        for (Document doc : store.values()) {
            if (doc.getContent().contains(message)) {
                tmp.add(doc);
            }
        }
        return tmp;
    }
}
