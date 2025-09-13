package demo.serialization.fury;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.fury.FuryBytesSerializer;

/**
 * @author noear 2025/9/13 created
 */
@Configuration
public class Demo4Config {
    @Bean
    public void config(FuryBytesSerializer serializer) throws Exception {
        serializer.bodyRequired();
    }
}