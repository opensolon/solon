package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2021/11/25 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class RestTest extends HttpTestBase {
    @Test
    public void get() throws Exception {
        assert path("/demo2a/rest/test?name=noear").get().equals("Get-noear");
    }

    @Test
    public void post() throws Exception {
        assert path("/demo2a/rest/test").data("name", "noear").post().equals("Post-noear");
    }

    @Test
    public void put() throws Exception {
        assert path("/demo2a/rest/test").data("name", "noear").put().equals("Put-noear");
    }

    @Test
    public void delete() throws Exception {
        assert path("/demo2a/rest/test").data("name", "noear").delete().equals("Delete-noear");
    }

    @Test
    public void patch() throws Exception {
        assert path("/demo2a/rest/test").data("name", "noear").patch().equals("Patch-noear");
    }
}
