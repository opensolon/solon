package demo.serialization.fory;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.fory.ForyBytesSerializer;

/**
 * @author noear 2025/9/13 created
 */
@Configuration
public class Demo4Config {
    @Bean
    public void config(ForyBytesSerializer serializer) throws Exception {
        serializer.bodyRequired();
    }
}