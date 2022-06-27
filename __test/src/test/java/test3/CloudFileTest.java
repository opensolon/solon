package test3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.Result;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

/**
 * @author noear 2021/4/7 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class CloudFileTest {
    @Test
    public void test() {
        if (CloudClient.file() == null) {
            System.err.println("This file service is not available");
            return;
        }

        String key = "test/" + Utils.guid();
        String val = "Hello world!";

        Result result = CloudClient.file().put(key, new Media(val));
        System.out.println(ONode.stringify(result));
        assert result.getCode() == Result.SUCCEED_CODE;


        String tmp = CloudClient.file().get(key).bodyAsString();
        assert val.equals(tmp);
    }

    @Test
    public void test2() throws Exception{
        if (CloudClient.file() == null) {
            System.err.println("This file service is not available");
            return;
        }

        String key = "test/" + Utils.guid() + ".png";
        File val = new File(Utils.getResource("test.png").getFile());
        String valMime = Utils.mime(val.getName());

        Result result = CloudClient.file().put(key, new Media(new FileInputStream(val), valMime));
        System.out.println(ONode.stringify(result));
        assert result.getCode() == Result.SUCCEED_CODE;
    }


    @Test
    public void test3_demo() {
        if (CloudClient.file() == null) {
            System.err.println("This file service is not available");
            return;
        }

        String key = "test/" + Utils.guid();

        String image_base64 = "";
        byte[] image_btys = Base64.getDecoder().decode(image_base64);

        Result result = CloudClient.file().put(key, new Media(image_btys, "image/jpeg"));
        System.out.println(ONode.stringify(result));
        assert result.getCode() == Result.SUCCEED_CODE;
    }
}
