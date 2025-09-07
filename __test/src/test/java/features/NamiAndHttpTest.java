package features;

import org.junit.jupiter.api.Test;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo5_rpc.HelloService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2025/5/7 created
 */
@SolonTest(App.class)
public class NamiAndHttpTest {
    //直接指定服务端地址
    @NamiClient(url = "http://localhost:8080/demo5/hello/")
    HelloService helloService;

    @Test
    public void hello() {
        assert helloService.hello("world", "1", "a").contains("world:1:a");
        assert helloService.hello("solon", "2", "b").contains("solon:2:b");
    }

    @Test
    public void test01() {
        List<String> ids = new ArrayList<>();
        ids.add("a");
        ids.add("b");
        assert helloService.test01(ids).equals("a,b");
    }

    @Test
    public void test02() throws IOException {
        UploadedFile file = new UploadedFile(MimeType.TEXT_PLAIN_VALUE,
                new ByteArrayInputStream("hello".getBytes()),
                "demo1.txt");

        assert helloService.test02(file).equals("demo1.txt");
    }

    @Test
    public void test03() throws IOException {
        assert helloService.test03().equals("test03");
    }

    @Test
    public void test04() throws IOException {
        assert helloService.test04(1, "test04").equals("1:\"test04\"");
    }
}
