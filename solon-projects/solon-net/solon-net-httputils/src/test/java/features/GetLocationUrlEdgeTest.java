package features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.impl.AbstractHttpUtils;

/**
 * Edge-case tests for AbstractHttpUtils.getLocationUrl().
 */
public class GetLocationUrlEdgeTest {

    @Test
    public void testEmptyLocation() {
        // location "" does not contain "://", does not start with "/".
        // refererPath "/page" does not end with "/", lastIndexOf('/') = 0,
        // substring(0, 1) = "/", location becomes "/" + "" = "/".
        Assertions.assertEquals("http://a.org/",
                AbstractHttpUtils.getLocationUrl("http://a.org/page", ""));
    }

    @Test
    public void testLocationWithQueryOnly() {
        // location "?query=1" does not contain "://", does not start with "/".
        // refererPath "/page" -> directory "/", location = "/" + "?query=1" = "/?query=1".
        Assertions.assertEquals("http://a.org/?query=1",
                AbstractHttpUtils.getLocationUrl("http://a.org/page", "?query=1"));
    }

    @Test
    public void testRefererWithoutPath() {
        // URI.create("http://a.org").getPath() returns "".
        // "/index.html" starts with "/", so redirectUrl = scheme://authority + location.
        Assertions.assertEquals("http://a.org/index.html",
                AbstractHttpUtils.getLocationUrl("http://a.org", "/index.html"));
    }

    @Test
    public void testAbsolutePath() {
        // "/new" starts with "/", so redirectUrl = "http://a.org/new".
        Assertions.assertEquals("http://a.org/new",
                AbstractHttpUtils.getLocationUrl("http://a.org/user/profile", "/new"));
    }

    @Test
    public void testFullUrl() {
        // "https://b.org/path" contains "://", so redirectUrl = the location itself.
        Assertions.assertEquals("https://b.org/path",
                AbstractHttpUtils.getLocationUrl("http://a.org", "https://b.org/path"));
    }
}