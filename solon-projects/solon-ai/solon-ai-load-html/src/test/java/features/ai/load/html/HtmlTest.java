package features.ai.load.html;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.rag.loader.HtmlSimpleLoader;
import org.noear.solon.net.http.HttpUtils;

/**
 * @author noear 2025/2/21 created
 */
public class HtmlTest {
    @Test
    public void case1() throws Exception {
       String html = HttpUtils.http("https://solon.noear.org/article/about").get();

        HtmlSimpleLoader loader = new HtmlSimpleLoader(html);

        System.out.println(loader.load());
    }
}
