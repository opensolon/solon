package org.noear.solon.ai.rag.splitter;

import org.noear.solon.ai.rag.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/2/18 created
 */
public class TestSplitter implements DocumentSplitter {
    @Override
    public List<Document> split(List<Document> documents) {
        List<Document> outs = new ArrayList<>();


        for (Document doc : documents) {
            splitDo(doc, outs);
        }

        return outs;
    }

    private void splitDo(Document in, List<Document> outs) {
        for (String chuck : splitText(in.getContent())) {
            if (chuck.length() > 0) {
                outs.add(new Document(chuck, in.getMetadata()));
            }
        }
    }

    protected String[] splitText(String text) {
        return text.split("\n");
    }
}
