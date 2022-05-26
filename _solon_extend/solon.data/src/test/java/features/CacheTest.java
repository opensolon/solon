package features;

import org.junit.Test;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.LocalCacheService;

/**
 * @author noear 2022/2/21 created
 */
public class CacheTest {

    CacheService cacheService = new LocalCacheService();

    @Test
    public void test1(){
        cacheService.store("1","world",100);

        assert "world".equals(cacheService.get("1"));
    }
}
