package demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.Result;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear 2021/10/18 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(DemoApp.class)
public class DemoTest {
    static Logger log = LoggerFactory.getLogger(DemoTest.class);

    final String contentBody = "test s3 rest api";
    final Media media = new Media(contentBody);
    final String key = "test0/test.txt";

    @Test
    public void test0() {
        long start = System.currentTimeMillis();

        //写入
        Result result = CloudClient.file().put(key, media);
        log.info("put result: {}", result);
        assert result.getCode() == 200;

        //获取
        Media getMedia = CloudClient.file().get(key);
        String getBodyString = getMedia.bodyAsString(true);
        log.info("getMedia body: {}", getBodyString);
        assert getBodyString.equals(contentBody);

        //删除
        result = CloudClient.file().delete(key);
        log.debug("delete result: {}", result);
        assert result.getCode() == 200;

        System.out.println("times: " + (System.currentTimeMillis() - start));
    }
}
