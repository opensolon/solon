package features.solon.generic8;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 *
 * @author noear 2026/2/11 created
 *
 */
@Configuration
public class Config {
    @Bean
    public Map<String, Object> map1() {
        HashMap<String, Object> tmp = new HashMap<>();
        tmp.put("a", "1");
        return tmp;
    }

    @Bean
    public Map<Integer, Object> map2() {
        HashMap<Integer, Object> tmp = new HashMap<>();
        tmp.put(1, "1");
        return tmp;
    }

    @Bean
    public void mapInject1(@Inject Map<Integer, Object> map) {
        System.out.println(map);
    }

    @Bean
    public void mapInject2(@Inject Map<String, Object> map) {
        System.out.println(map);
    }

//    @Bean
//    public void mapInject3(@Inject Map<?, ?> map) {
//        System.out.println(map);
//    }

    @Bean
    public void mapInject4(@Inject List<Map<?, ?>> mapList) {
        System.out.println(mapList);
    }
}