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
public class GiteeaiTest {
    private static final Logger log = LoggerFactory.getLogger(GiteeaiTest.class);
    private static final String apiUrl = "https://ai.gitee.com/v1/images/generations";
    private static final String apiKey = "PE6JVMP7UQI81GY6AZ0J8WEWWLFHWHROG15XUP18";
    private static final String model = "stable-diffusion-3.5-large-turbo";//"DeepSeek-V3"; //deepseek-reasoner//deepseek-chat

    @Test
    public void case1() throws IOException {
        ImageModel chatModel = ImageModel.of(apiUrl).apiKey(apiKey).model(model).build();

        //一次性返回
        ImageResponse resp = chatModel.prompt("a white siamese cat")
                .options(o -> o.size("1024x1024"))
                .call();

        byte[] bytes = Base64.getDecoder().decode(resp.getImage().getB64Json());
        File file = new File("/Users/noear/Downloads/ai-tmp1.jpg");
        file.createNewFile();
        file.deleteOnExit();
        IoUtil.transferTo(new ByteArrayInputStream(bytes), new FileOutputStream(file));

        //打印消息
        //log.info("{}", resp.getImage());
    }
}