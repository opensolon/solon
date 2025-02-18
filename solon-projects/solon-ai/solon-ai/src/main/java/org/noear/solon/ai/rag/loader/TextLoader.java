package org.noear.solon.ai.rag.loader;

import org.noear.solon.ai.rag.Document;
import org.noear.solon.core.util.ResourceUtil;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author noear
 * @since 3.1
 */
public class TextLoader implements DocumentLoader {
    private final URL url;

    public TextLoader(String uri) {
        this.url = ResourceUtil.findResource(uri);
    }

    @Override
    public List<Document> load() {
        try {
            String temp = ResourceUtil.getResourceAsString(url);
            return Arrays.asList(new Document(temp));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
