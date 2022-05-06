package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

/**
 * @author noear 2020/12/20 created
 */

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpParam2Test extends HttpTestBase {
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
