package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.RenderManager;

/**
 * @author noear 2021/10/9 created
 */
@Configuration
public class Config {
    @Bean
    public void jsonRender(){
        //
        // 替换掉默认的json渲染器
        //
        RenderManager.mapping("@json", new FastjsonRender2(false));
        //RenderManager.mapping("@type_json", new FastjsonRender2(true));
    }
}
