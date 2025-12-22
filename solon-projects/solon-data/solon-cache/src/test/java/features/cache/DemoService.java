package features.cache;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Param;
import org.noear.solon.data.annotation.Cache;

/**
 *
 * @author noear 2025/12/22 created
 *
 */

@Component
public class DemoService {
    @Cache(key = "#{id}", seconds = 1)
    public String getName(@Param("id") String i) {
        return String.valueOf(System.currentTimeMillis());
    }
}
