package features.ai.read.md;

import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.loader.MarkdownSplitter;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.net.http.HttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

/**
 * @author chengchuanyao
 * @date 2025/2/21 17:18
 */
public class MarkdownSplitterTest {

    public static void main(String[] args) throws IOException {
        String md = HttpUtils.http("https://solon.noear.org/article/about?format=md").get();

//        MarkdownLoader loader = new MarkdownLoader();
//        MarkdownLoader loader = new MarkdownLoader("\n\n", 500, 50);
        MarkdownSplitter loader = new MarkdownSplitter("\n\n", 500, 50, EnumSet.of(MarkdownSplitter.PatternType.SPACE));
        List<Document> split = loader.split(md);
        split.forEach(s-> System.out.println("分段：\n"+s.getContent()));
    }
}
