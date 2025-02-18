package org.noear.solon.ai.rag.splitter;

import org.noear.solon.ai.rag.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文本分割器
 *
 * @author noear
 * @since 3.1
 */
public class TextSplitter implements DocumentSplitter {

    @Override
    public List<Document> split(List<Document> documents) {
        List<Document> outs = new ArrayList<>();

        for (Document doc : documents) {
            splitDocument(doc, outs);
        }

        return outs;
    }

    protected List<Document> splitDocument(Document in, List<Document> outs) {
        for (String chuck : splitText(in.getContent())) {
            if (chuck.length() > 0) {
                outs.add(new Document(chuck, in.getMetadata()));
            }
        }

        return outs;
    }

    protected List<String> splitText(String text) {
        return Arrays.asList(text.split("\n"));
    }
}
