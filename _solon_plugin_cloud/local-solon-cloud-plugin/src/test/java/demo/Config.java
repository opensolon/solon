package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.impl.CloudI18nBundleFactory;
import org.noear.solon.i18n.I18nBundleFactory;

import java.util.Properties;

/**
 * @author noear 2022/11/21 created
 */
@Configuration
public class Config {
    @Bean
    public void init1(@CloudConfig("demo-db") Properties props) {
        System.out.println("云端配置服务直接注入的：" + props);
    }

    //将 i18n 切换为云端服务
    @Bean
    public I18nBundleFactory init2(){
       return new CloudI18nBundleFactory();
    }
}
