package demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.Result;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 等風來再離開 2022/11/29 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(DemoApp.class)
public class DemoTest {
    static Logger log = LoggerFactory.getLogger(DemoTest.class);

    final String contentBody = "test s3 rest api";
    final Media media = new Media(contentBody);
    final String key = "a.txt";

    final String bucket = "local";

    @Test
    public void test0() {
        long start = System.currentTimeMillis();
        Result result = CloudClient.file().put(key, media);

        System.out.println("put result:" + ONode.stringify(result));
        assert result.getCode() == 200;

        Media getMedia = CloudClient.file().get(key);
        String getBodyString = getMedia.bodyAsString();
        System.out.println("getBodyString" + getBodyString);
        assert getBodyString.equals(contentBody);

        result = CloudClient.file().delete(key);
        System.out.println("delete result:" + ONode.stringify(result));
        assert result.getCode() == 200;


        System.out.println("times: " + (System.currentTimeMillis() - start));
    }
}
