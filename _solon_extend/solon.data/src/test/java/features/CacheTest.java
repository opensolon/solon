package features;

import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.LocalCacheService;

/**
 * @author noear 2022/2/21 created
 */
public class CacheTest {

    public static void main(String[] args){
        CacheService cacheService = new LocalCacheService();

        cacheService.store("1","world",100);

        assert "world".equals(cacheService.get("1"));
    }
}
