package demo;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.view.beetl.BeetlRender;

/**
 * @author noear 2024/9/15 created
 */
@Configuration
public class Config {
    @Managed
    public void configure(BeetlRender render){
        render.getProvider();
        render.getProviderOfDebug();
    }
}
