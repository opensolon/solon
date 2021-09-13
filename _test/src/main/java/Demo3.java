import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.Aop;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2021/9/10 created
 */
@Configuration
public class Demo3 {
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
            Aop.getAsyn(Map.class, bw->{
                //通过异常获取bean，不需要关注时机；但构造处理要放在回调里
                this.map = bw.raw();
            });
        }
    }
}
