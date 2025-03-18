package org.noear.solon.ai.rag.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentLoader;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.loader.HtmlSimpleLoader;
import org.noear.solon.ai.rag.loader.MarkdownLoader;
import org.noear.solon.ai.rag.splitter.RegexTextSplitter;
import org.noear.solon.ai.rag.splitter.SplitterPipeline;
import org.noear.solon.ai.rag.splitter.TokenSizeTextSplitter;
import org.noear.solon.net.http.HttpUtils;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestUtils {
    public static EmbeddingModel getEmbeddingModel() {
        final String apiUrl = "http://127.0.0.1:11434/api/embed";
        final String provider = "ollama";
        final String model = "all-minilm";//

        return EmbeddingModel.of(apiUrl).provider(provider).model(model).build();
    }
}

public class QdrantRepositoryTest {
    private QdrantClient client = new QdrantClient(QdrantGrpcClient.newBuilder("localhost", 6334, false).build());
    private String collectionName = "solonAiRepo";
    private QdrantRepository repository;

    @BeforeEach
    public void setup() throws Exception {
        repository = new QdrantRepository(TestUtils.getEmbeddingModel(), client, collectionName);

        load(repository, "https://solon.noear.org/article/about?format=md");
        load(repository, "https://h5.noear.org/more.htm");
        load(repository, "https://h5.noear.org/readme.htm");
    }

    @AfterEach
    public void cleanup() {
        repository.dropRepository();
    }

    @Test
    public void case1_search() throws Exception {
        List<Document> list = repository.search("solon");
        assert list.size() == 4;

        list = repository.search("vert.x");
        assert list.size() == 0;

        Document doc = new Document("Test content");
        repository.insert(Collections.singletonList(doc));
        String key = doc.getId();

        assertTrue(repository.exists(key), "Document should exist after storing");

        repository.delete(doc.getId());
        assertFalse(repository.exists(key), "Document should not exist after removal");
    }

    private void load(RepositoryStorable repository, String url) throws IOException {
        String text = HttpUtils.http(url).get();

        DocumentLoader loader = null;
        if (text.contains("</html>")) {
            loader = new HtmlSimpleLoader(text.getBytes(StandardCharsets.UTF_8));
        } else {
            loader = new MarkdownLoader(text.getBytes(StandardCharsets.UTF_8));
        }

        List<Document> documents = new SplitterPipeline().next(new RegexTextSplitter())
                .next(new TokenSizeTextSplitter(500)).split(loader.load());

        repository.insert(documents);
    }
}
