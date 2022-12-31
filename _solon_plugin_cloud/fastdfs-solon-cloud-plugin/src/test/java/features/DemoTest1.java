package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.annotation.TestPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@TestPropertySource("app-test1.yml")
@RunWith(SolonJUnit4ClassRunner.class)
public class DemoTest1 {
    static Logger log = LoggerFactory.getLogger(DemoTest1.class);

    final String contentBody = "test s3 rest api";
    final Media media = new Media(contentBody);
    final String key = "test0/test.txt";

    @Test
    public void test0() throws IOException {
        long start = System.currentTimeMillis();

        //写入
        Result result = CloudClient.file().put(key, media);
        log.info("put result: {}", result);
        assert result.getCode() == 200;

        //获取
        Media getMedia = CloudClient.file().get(key);
        String getBodyString = getMedia.bodyAsString(true);
        log.info("getMedia size:{}, body: {}", getMedia.contentSize(), getBodyString);
        assert getBodyString.equals(contentBody);

        //删除
        result = CloudClient.file().delete(key);
        log.debug("delete result: {}", result);
        assert result.getCode() == 200;

        System.out.println("times: " + (System.currentTimeMillis() - start));
    }

    //下载文件
    public DownloadedFile get(String bucket, String key) {
        Media tmp = CloudClient.file().get(bucket, key);

        return new DownloadedFile(tmp.contentType(), tmp.body(), key);
    }

    //上传文件
    public void upload(String bucket, UploadedFile file) {
        Media tmp = new Media(file.getContent(), file.getContentType());
        CloudClient.file().put(bucket, file.getName(), tmp);
    }
}
