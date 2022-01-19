package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.Map;

/**
 * @author noear 2021/5/28 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class InjectTest {
    @Inject(value = "${username}", autoRefreshed = true)
    String username;


    @Inject("${formattest.text}")
    String formattest;

    @Inject("${formattest.text2}")
    String formattest2;


    @Inject("map1")
    Map<String, Object> map;

    @Test
    public void test1() {
        assert "noear".equals(username);

        Solon.cfg().setProperty("username", "xxx");

        assert "xxx".equals(username);

        assert map != null;
        assert map.get("1").equals(1);
    }

    @Test
    public void test2() {
        System.out.println(formattest);
        assert "512m/-/en_US".equals(formattest);

        assert "${aaaa.bbb}".equals(formattest2);
    }
}
