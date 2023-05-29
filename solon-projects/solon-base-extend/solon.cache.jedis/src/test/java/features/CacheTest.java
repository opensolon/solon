package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.test.SolonJUnit4ClassRunner;

/**
 * @author noear 2023/2/16 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class CacheTest {
    @Inject
    CacheService cacheService;

    @Test
    public void test() throws Exception{
        cacheService.store("test", "1", 1);
        assert "1".equals(cacheService.get("test"));
        Thread.sleep(2000);
        assert cacheService.get("test") == null;


        UserM userM = new UserM();
        userM.id = 12;
        userM.name = "test";

        cacheService.store("test", userM, 1);
        assert userM.id == ((UserM)cacheService.get("test")).id;
        Thread.sleep(2000);
        assert cacheService.get("test") == null;
    }

    @Test
    public void test2() throws Exception{
        cacheService.store("test", "1", 0);
        assert "1".equals(cacheService.get("test"));
        cacheService.remove("test");
        assert cacheService.get("test") == null;


        UserM userM = new UserM();
        userM.id = 12;
        userM.name = "test";

        cacheService.store("test", userM, 0);
        assert userM.id == ((UserM)cacheService.get("test")).id;
        cacheService.remove("test");
        assert cacheService.get("test") == null;
    }
}
