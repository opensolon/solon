package demo.serialization.protostuff;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.protostuff.ProtostuffBytesSerializer;

/**
 * @author noear 2025/9/13 created
 */
@Configuration
public class Demo4Config {
    @Bean
    public void config(ProtostuffBytesSerializer serializer) {
        serializer.bodyRequired();
    }
}
