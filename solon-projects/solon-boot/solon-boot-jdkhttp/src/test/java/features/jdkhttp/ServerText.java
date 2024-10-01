package features.jdkhttp;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

@SolonTest(App.class)
public class ServerText extends HttpTester {

    @Test
    public void test() throws Exception {
        assert "hello".equals(path("/hello").get());
    }
}
