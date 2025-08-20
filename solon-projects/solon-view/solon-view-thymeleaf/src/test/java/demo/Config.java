package demo;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.view.thymeleaf.ThymeleafRender;

/**
 * @author noear 2024/9/15 created
 */
@Configuration
public class Config {
    @Managed
    public void configure(ThymeleafRender render){
        render.getProvider();
    }
}
