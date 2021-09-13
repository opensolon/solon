import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Aop;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2021/9/10 created
 */
@Configuration
public class Demo2 {
    @Bean
    public Map<String, Object> map() {
        Map<String, Object> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);

        return map;
    }

    public class Xxxx {
        private Map<String, Object> map;

        public Xxxx() {
            //要求Map的Bean已经存在（时机要安排好）
            this.map = Aop.getOrNull(Map.class);
        }
    }
}
