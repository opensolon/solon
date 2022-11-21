package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.annotation.CloudConfig;

import java.util.Properties;

/**
 * @author noear 2022/11/21 created
 */
@Configuration
public class Config {
    @Bean
    public void init(@CloudConfig("demo-db") Properties props) {
        System.out.println("配置服务直接注入的：" + props);
    }
}
