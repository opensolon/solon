package features.ai.image;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.image.ImageModel;
import org.noear.solon.ai.image.ImageResponse;
import org.noear.solon.core.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @author noear 2025/1/28 created
 */
public class DashcsopeTest {
    private static final Logger log = LoggerFactory.getLogger(features.ai.chat.DashscopeTest.class);
    private static final String apiUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis";
    private static final String apiKey = "sk-1ffe449611a74e61ad8e71e1b35a9858";
    private static final String provider = "dashscope";
    private static final String model = "wanx2.1-t2i-turbo";//"llama3.2"; //deepseek-r1:1.5b;

    @Test
    public void case1() throws IOException {
        ImageModel chatModel = ImageModel.of(apiUrl)
                .apiKey(apiKey)
                .provider(provider)
                .model(model)
                .headerSet("X-DashScope-Async", "enable")
                .build();

        //一次性返回
        ImageResponse resp = chatModel.prompt("a white siamese cat")
                .options(o -> o.size("1024x1024"))
                .call();

        //打印消息
        log.info("{}", resp.getImage());
        assert resp.getImage().getUrl() != null;
    }
}