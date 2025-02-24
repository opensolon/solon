package features.ai.read.md;

import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.loader.MarkdownLoader;
import org.noear.solon.net.http.HttpUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MarkdownLoaderTest {

    public static void main(String[] args) throws IOException {
        String md = HttpUtils.http("https://solon.noear.org/article/about?format=md").get();

        MarkdownLoader markdownLoader = new MarkdownLoader(md.getBytes(StandardCharsets.UTF_8))
                .options(o -> o.horizontalLineAsNew(true)
                        .blockquoteAsNew(true)
                        .codeBlockAsNew(true));

        int i = 0;
        for (Document doc : markdownLoader.load()) {
            System.out.println((i++) + ":------------------------------------------------------------");
            System.out.println(doc);
        }
    }
}
