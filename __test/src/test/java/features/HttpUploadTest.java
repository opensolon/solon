package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author noear 2022/3/22 created
 */
@SolonTest(App.class)
public class HttpUploadTest extends HttpTester {
    @Test
    public void upload() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

        assert path("/demo3/upload/f1")
                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
                .post().contains("成功：装修-水电-视频.mp4");
    }

    @Test
    public void upload2() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));
        InputStream inputStream2 = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

        String rst = path("/demo3/upload/f1")
                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
                .data("file2", "测试2.mp4", inputStream2, "video/mp4")
                .post();

        assert rst.contains("成功：装修-水电-视频.mp4");
        assert rst.contains("测试2.mp4");
    }

//    @Test
//    public void upload_empty() throws IOException {
//        InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));
//
//        assert path("/demo3/upload/f11")
//                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
//                .data("userName", "noear")
//                .post().contains("我没接数据：）");
//
//
//        inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));
//
//        assert path("/demo3/upload/f11_2")
//                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
//                .data("userName", "noear")
//                .post().contains("noear");
//    }

    @Test
    public void upload_param() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

        assert path("/demo3/upload/f12")
                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
                .data("userName", "noear")
                .post().startsWith("noear");
    }

    @Test
    public void upload_param_1() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

        assert path("/demo3/upload/f12_1")
                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
                .data("userName", "noear")
                .post().startsWith("noear");
    }

    @Test
    public void upload_param_2() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

        assert path("/demo3/upload/f13")
                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
                .data("userName", "noear")
                .post().startsWith("noear");
    }

    @Test
    public void upload_param_2_2() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

        assert path("/demo3/upload/f13_2")
                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
                .data("userName", "noear")
                .post().startsWith("noear");
    }

    @Test
    public void upload_param_f3() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

        assert path("/demo3/upload/f3")
                .data("file", "装修-水电-视频.mp4", inputStream, "video/mp4")
                .post().equals("1");
    }
}
