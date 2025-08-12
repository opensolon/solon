package features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.impl.AbstractHttpUtils;

/**
 *
 * @author noear 2025/8/12 created
 *
 */
public class NewLocationUrlTest {
    @Test
    public void case1() {
        //绝对路径
        Assertions.assertEquals("http://noear.org/index.html",
                AbstractHttpUtils.getLocationUrl("http://noear.org", "/index.html"));

        Assertions.assertEquals("http://noear.org/index.html",
                AbstractHttpUtils.getLocationUrl("http://noear.org/user/", "/index.html"));

        ///

        Assertions.assertEquals("http://noear.org/test/index.html?a=1",
                AbstractHttpUtils.getLocationUrl("http://noear.org", "/test/index.html?a=1"));

        Assertions.assertEquals("http://noear.org/test/index.html?a=1",
                AbstractHttpUtils.getLocationUrl("http://noear.org/user/", "/test/index.html?a=1"));
    }

    @Test
    public void case2() {
        //相对路径
        Assertions.assertEquals("http://noear.org/user/index.html",
                AbstractHttpUtils.getLocationUrl("http://noear.org/user/", "index.html"));

        Assertions.assertEquals("http://noear.org/index.html",
                AbstractHttpUtils.getLocationUrl("http://noear.org/user", "index.html"));

        Assertions.assertEquals("http://noear.org/user/index.html",
                AbstractHttpUtils.getLocationUrl("http://noear.org/user/test", "index.html"));

        ///

        Assertions.assertEquals("http://noear.org/user/test/index.html?a=1",
                AbstractHttpUtils.getLocationUrl("http://noear.org/user/", "test/index.html?a=1"));

        Assertions.assertEquals("http://noear.org/test/index.html?a=1",
                AbstractHttpUtils.getLocationUrl("http://noear.org/user", "test/index.html?a=1"));

        Assertions.assertEquals("http://noear.org/user/test/index.html?a=1",
                AbstractHttpUtils.getLocationUrl("http://noear.org/user/test", "test/index.html?a=1"));
    }

    @Test
    public void csae3() {
        //完成路径
        Assertions.assertEquals("http://a.org/index.html",
                AbstractHttpUtils.getLocationUrl("http://noear.org", "http://a.org/index.html"));

        Assertions.assertEquals("https://a.org/index.html?a=1",
                AbstractHttpUtils.getLocationUrl("http://noear.org", "https://a.org/index.html?a=1"));
    }
}
