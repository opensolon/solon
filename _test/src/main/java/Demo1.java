import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2021/9/10 created
 */
@Configuration
public class Demo1 {
    @Bean
    public Map<String, Object> map() {
        Map<String, Object> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);

        return map;
    }

    @Bean
    public Xxxx xxxx(@Inject Map<String, Object> map){
        //通过配置器，获取构造函数所需
        return new Xxxx(map);
    }


    public class Xxxx {
        private Map<String, Object> map;

        public Xxxx(Map<String, Object> map) {
            this.map = map;
        }
    }
}
