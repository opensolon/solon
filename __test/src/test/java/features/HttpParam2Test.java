package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;

/**
 * @author noear 2020/12/20 created
 */

@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class HttpParam2Test extends HttpTester {
    @Override
    public boolean enablePrint() {
        return true;
    }

    @Test
    public void test1_required() throws IOException {
        assert path("/demo2/param2/anno/required").execAsCode("GET") == 400;

        assert path("/demo2/param2/anno/required?name=hi").execAsCode("GET") != 400;
    }

    @Test
    public void test2() throws IOException {
        assert path("/demo2/param2/anno/required_def").get().equals("noear");
    }

    @Test
    public void test3_def() throws IOException {
        assert path("/demo2/param2/anno/def").get().equals("noear");
    }

    @Test
    public void test4() throws IOException {
        assert path("/demo2/param2/anno/name?n2=noear").get().equals("noear");

        assert path("/demo2/param2/anno/name?n2=hi").get().equals("hi");
    }
}
