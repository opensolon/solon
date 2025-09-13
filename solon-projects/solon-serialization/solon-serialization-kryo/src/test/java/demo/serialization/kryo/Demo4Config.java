package demo.serialization.kryo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.kryo.KryoBytesSerializer;

/**
 * @author noear 2025/9/13 created
 */
@Configuration
public class Demo4Config {
    @Bean
    public void config(KryoBytesSerializer serializer) {
        serializer.bodyRequired();
    }
}