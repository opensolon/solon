package demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.Result;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2021/10/18 created
 */
@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(DemoApp.class)
public class DemoTest {
    final String contentBody = "test s3 rest api";
    final Media media = new Media(contentBody);
    final String key = "test0/test.txt";

    @Test
    public void test0() {
        long start = System.currentTimeMillis();

        Result result = CloudClient.file().put(key, media);
        log.info("put result: {}", result);
        assert result.getCode() == 200;

        Media getMedia = CloudClient.file().get(key);
        String getBodyString = getMedia.bodyAsString();
        log.info("getMedia body: {}", getBodyString);
        assert getBodyString.equals(contentBody);

        result = CloudClient.file().delete(key);
        log.debug("delete result: {}", result);
        assert result.getCode() == 200;

        System.out.println("times: " + (System.currentTimeMillis() - start));
    }
}
