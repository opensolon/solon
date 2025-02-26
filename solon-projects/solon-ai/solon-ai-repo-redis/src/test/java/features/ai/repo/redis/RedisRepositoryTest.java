package features.ai.repo.redis;

import org.junit.jupiter.api.Test;
import org.noear.redisx.RedisClient;
import org.noear.solon.Solon;
import org.noear.solon.ai.embedding.EmbeddingConfig;
import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.DocumentLoader;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.loader.HtmlSimpleLoader;
import org.noear.solon.ai.rag.loader.MarkdownLoader;
import org.noear.solon.ai.rag.repository.RedisRepository;
import org.noear.solon.ai.rag.splitter.RegexTextSplitter;
import org.noear.solon.ai.rag.splitter.SplitterPipeline;
import org.noear.solon.ai.rag.splitter.TokenSizeTextSplitter;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.test.SolonTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author noear 2025/2/26 created
 */
@SolonTest
public class RedisRepositoryTest {
    @Test
    public void case1() throws Exception {

        EmbeddingConfig config = Solon.cfg().toBean("solon.ai.embed.bgem3", EmbeddingConfig.class);
        EmbeddingModel embeddingModel = EmbeddingModel.of(config).build();

        RedisClient redisClient = Solon.cfg().toBean("solon.ai.repo.redis", RedisClient.class);
        RedisRepository repo = new RedisRepository(embeddingModel, redisClient.openSession().jedis());


        load(repo, "https://solon.noear.org/article/about?format=md");
        load(repo, "https://h5.noear.org/more.htm");
        load(repo, "https://h5.noear.org/readme.htm");

        List<Document> documents = repo.search("solon 是谁开发的？");

        documents.forEach(doc -> {
            System.out.println(doc);
        });
    }

    private void load(RepositoryStorable repository, String url) throws IOException {
        String text = HttpUtils.http(url).get();

        //1.加载文档（测试用）
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

        repository.store(documents); //（推入文档）
    }
}
