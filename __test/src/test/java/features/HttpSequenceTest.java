package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;

/**
 * @author noear 2024/9/25 created
 */
@SolonTest(App.class)
public class HttpSequenceTest extends HttpTester {
    @Override
    public boolean enablePrint() {
        return true;
    }

    @Test
    public void sequence_cookie() throws IOException {
        assert path("/demo2/sequence/cookie")
                .cookieAdd("v1", "a")
                .cookieAdd("v1", "b")
                .cookieAdd("v1", "c")
                .get().equals("a;c");
    }


    @Test
    public void sequence_cookie1() throws IOException {
        assert path("/demo2/sequence/cookie")
                .cookieAdd("v1", "a")
                .cookieAdd("v2", "b")
                .get().equals("a;a");
    }

    @Test
    public void sequence_header() throws IOException {
        assert path("/demo2/sequence/header")
                .headerAdd("v1", "a")
                .headerAdd("v1", "b")
                .headerAdd("v1", "c")
                .get().equals("a;c");
    }

    @Test
    public void sequence_header1() throws IOException {
        assert path("/demo2/sequence/header")
                .headerAdd("v1", "a")
                .headerAdd("v2", "b")
                .get().equals("a;a");
    }

    @Test
    public void sequence_param() throws IOException {
        assert path("/demo2/sequence/param")
                .data("v1", "a")
                .data("v1", "b")
                .data("v1", "c")
                .post().equals("a;c");
    }

    @Test
    public void sequence_param1() throws IOException {
        assert path("/demo2/sequence/param")
                .data("v1", "a")
                .data("v2", "b")
                .post().equals("a;a");
    }
}
