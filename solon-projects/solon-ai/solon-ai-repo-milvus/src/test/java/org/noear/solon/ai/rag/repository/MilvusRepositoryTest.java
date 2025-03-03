package org.noear.solon.ai.rag.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentLoader;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.loader.HtmlSimpleLoader;
import org.noear.solon.ai.rag.loader.MarkdownLoader;
import org.noear.solon.ai.rag.splitter.RegexTextSplitter;
import org.noear.solon.ai.rag.splitter.SplitterPipeline;
import org.noear.solon.ai.rag.splitter.TokenSizeTextSplitter;
import org.noear.solon.net.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MilvusRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(MilvusRepositoryTest.class);
    private static String milvusUri = "http://localhost:19530";

    private ConnectConfig connectConfig = ConnectConfig.builder()
            .uri(milvusUri)
            .build();
    private MilvusClientV2 client = new MilvusClientV2(connectConfig);
    private String collectionName = "solonAiRepo";
    private MilvusRepository repository;

    @BeforeEach
    public void setup() throws Exception {
        repository = new MilvusRepository(
                TestUtils.getEmbeddingModel(),
                client,
                collectionName); //3.初始化知识库

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


        /// /////////////////////////////

        // 准备并存储文档，显式指定 ID
        Document doc = new Document("Test content");
        repository.insert(Collections.singletonList(doc));
        String key = doc.getId();

        // 验证存储成功
        assertTrue(repository.exists(key), "Document should exist after storing");

        // 删除文档
        repository.delete(doc.getId());

        // 验证删除成功
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

        List<Document> documents = new SplitterPipeline() //2.分割文档（确保不超过 max-token-size）
                .next(new RegexTextSplitter())
                .next(new TokenSizeTextSplitter(500))
                .split(loader.load());

        repository.insert(documents); //（推入文档）
    }
}