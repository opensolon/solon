package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.RoutingTableDefault;

/**
 *
 * @author noear 2025/12/3 created
 *
 */
public class RouterTableTest {
    @Test
    public void case1() {
        RoutingTableDefault<String> router = new RoutingTableDefault<>();

        router.add("/**/$*", MethodType.GET, 0, null, "a");
        router.add("/**/@*", MethodType.GET, 0, null, "b");

        assert "a".equals(router.matchOne("/log/list/$你好", MethodType.GET));
        assert "b".equals(router.matchOne("/log/list/@你好", MethodType.GET));
    }
}
