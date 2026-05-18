package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.javabin.JavabinSerializer;

/**
 *
 * @author noear 2026/5/18 created
 *
 */
@Configuration
public class DemoConfig {
    @Bean
    public void demo(JavabinSerializer serializer) {
        //模式示例
        serializer.classFilter().allow("demo.");

        //全允许示例
        serializer.classFilter().allowAll(true);
    }
}
