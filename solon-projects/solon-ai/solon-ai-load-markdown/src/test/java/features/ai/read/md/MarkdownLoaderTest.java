package features.ai.read.md;

import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.loader.MarkdownLoader;
import org.noear.solon.net.http.HttpUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MarkdownLoaderTest {

    public static void main(String[] args) throws IOException {
        String md = HttpUtils.http("https://solon.noear.org/article/about?format=md").get();

        MarkdownLoader markdownLoader = new MarkdownLoader(() -> new ByteArrayInputStream(md.getBytes()));

        for (Document doc : markdownLoader.load()) {
            System.out.println("------------------------------------------------------------");
            System.out.println(doc);
        }
    }
}
