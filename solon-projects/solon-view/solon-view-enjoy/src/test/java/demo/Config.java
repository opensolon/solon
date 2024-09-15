package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.view.enjoy.EnjoyRender;

/**
 * @author noear 2024/9/15 created
 */
@Configuration
public class Config {
    @Bean
    public void configure(EnjoyRender render){
        render.getProvider();
        render.getProviderOfDebug();
    }
}
