package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2021/3/27 created
 */
@SolonTest(App.class)
public class PropDefTest {
    @Inject("${test.name:noear}")
    String name;


    @Inject("${test.url:http://solon.noear.org}")
    String url;

    @Test
    public void test(){
        assert "noear".equals(name);
        assert "http://solon.noear.org".equals(url);
    }
}
