package features;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.data.cache.CacheService;

/**
 * @author noear 2022/4/15 created
 */
@Configuration
public class App {
    public static void main(String[] args) throws Exception {
        Solon.start(App.class, args);

        CacheService cacheService = Solon.context().getBean(CacheService.class);

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
}
