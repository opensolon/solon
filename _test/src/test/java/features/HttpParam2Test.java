package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

/**
 * @author noear 2020/12/20 created
 */

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpParam2Test extends HttpTest {
    @Test
    public void test1() throws IOException {
        assert getStatus("/demo2/param2/anno/required") == 400;

        assert getStatus("/demo2/param2/anno/required?name=hi") != 400;
    }

    @Test
    public void test2() throws IOException {
        assert get("/demo2/param2/anno/required_def").equals("noear");
    }

    @Test
    public void test3() throws IOException {
        assert get("/demo2/param2/anno/def").equals("noear");
    }

    @Test
    public void test4() throws IOException {
        assert get("/demo2/param2/anno/name?n2=noear").equals("noear");

        assert get("/demo2/param2/anno/name?n2=hi").equals("hi");
    }
}
