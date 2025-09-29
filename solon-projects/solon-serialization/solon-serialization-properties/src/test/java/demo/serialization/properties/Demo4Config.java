package demo.serialization.properties;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.properties.PropertiesStringSerializer;

/**
 * @author noear 2025/9/13 created
 */
@Configuration
public class Demo4Config {
    @Bean
    public void config(PropertiesStringSerializer serializer) {
        //允许 get 请求处理（默认为 true）
        serializer.allowGet(true);

        //允许 post form 请求处理（默认为 false）
        serializer.allowPostForm(false);
    }
}