package features.web.staticfiles;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.SolonTest;
import org.noear.solon.web.staticfiles.StaticConfig;

import static org.junit.jupiter.api.Assertions.*;

@SolonTest
public class StaticConfigTest {

    @Test
    public void testStaticConfig() {
        int original = StaticConfig.getCacheMaxAge();

        StaticConfig.setCacheMaxAge(3600);
        assertEquals(3600, StaticConfig.getCacheMaxAge());

        StaticConfig.setCacheMaxAge(-1);
        assertEquals(-1, StaticConfig.getCacheMaxAge());

        StaticConfig.setCacheMaxAge(original);
        assertEquals(original, StaticConfig.getCacheMaxAge());

        assertTrue(StaticConfig.isEnable());

        assertEquals("static/", StaticConfig.RES_STATIC_LOCATION);
        assertEquals("WEB-INF/static/", StaticConfig.RES_WEB_INF_STATIC_LOCATION);
        assertEquals("solon.staticfiles.enable", StaticConfig.PROP_ENABLE);
        assertEquals("solon.staticfiles.cacheMaxAge", StaticConfig.PROP_CACHE_MAX_AGE);
        assertEquals("solon.staticfiles.mappings", StaticConfig.PROP_MAPPINGS);

        new StaticConfig();
    }
}
