package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.AbstractHttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class HttpTest3 extends AbstractHttpTester {

    @Test
    public void test2d_2() throws IOException {
        assert path("/demo2/param/body").bodyTxt("name=xxx").post().equals("name=xxx");
        assert path("/demo2/param/body?name=xxx").get().equals("");
        assert path("/demo2/param/body").bodyTxt("name=xxx").post().equals("name=xxx");
    }
}
