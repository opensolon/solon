package features.ai.rag;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.splitter.TextSplitter;
import org.noear.solon.ai.rag.splitter.TokenTextSplitter;
import org.noear.solon.net.http.HttpUtils;

import java.util.List;

/**
 * @author noear 2025/2/18 created
 */
public class DocTest {
    @Test
    public void case1() throws Exception {
        String text = HttpUtils.http("https://solon.noear.org/article/about?format=md").get();

        TextSplitter textSplitter = new TextSplitter();
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> documents = tokenTextSplitter.split(textSplitter.split(text));

        System.out.println(documents);
    }
}
