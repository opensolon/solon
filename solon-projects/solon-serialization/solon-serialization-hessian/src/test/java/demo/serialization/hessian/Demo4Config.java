package demo.serialization.hessian;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.hessian.HessianBytesSerializer;

/**
 * @author noear 2025/9/13 created
 */
@Configuration
public class Demo4Config {
    @Bean
    public void config(HessianBytesSerializer serializer) {
        serializer.bodyRequired();
    }
}
