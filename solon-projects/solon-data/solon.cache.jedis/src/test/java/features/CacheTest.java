package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2023/2/16 created
 */
@SolonTest
public class CacheTest {
    @Inject
    CacheService cacheService;

    @Test
    public void test() throws Exception {
        cacheService.store("test", "1", 1);
        assert "1".equals(cacheService.get("test", String.class));
        Thread.sleep(2000);
        assert cacheService.get("test", String.class) == null;


        UserM userM = new UserM();
        userM.id = 12;
        userM.name = "test";

        cacheService.store("test", userM, 1);
        assert userM.id == cacheService.get("test", UserM.class).id;
        Thread.sleep(2000);
        assert cacheService.get("test", UserM.class) == null;
    }

    @Test
    public void test2() throws Exception {
        cacheService.store("test", "1", 0);
        assert "1".equals(cacheService.get("test", String.class));
        cacheService.remove("test");
        assert cacheService.get("test", String.class) == null;


        UserM userM = new UserM();
        userM.id = 12;
        userM.name = "test";

        cacheService.store("test", userM, 0);
        assert userM.id == cacheService.get("test", UserM.class).id;
        cacheService.remove("test");
        assert cacheService.get("test", UserM.class) == null;
    }
}
