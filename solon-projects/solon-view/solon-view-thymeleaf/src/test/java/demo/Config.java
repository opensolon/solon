package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.view.thymeleaf.ThymeleafRender;

/**
 * @author noear 2024/9/15 created
 */
@Configuration
public class Config {
    @Bean
    public void configure(ThymeleafRender render){
        render.getProvider();
    }
}
