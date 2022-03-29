package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.socketd.ListenerPipeline;

/**
 * @author noear 2022/3/29 created
 */
@Configuration
public class Config {
    @Bean
    public void filterListen(ListenerPipeline pipeline){
        pipeline.prevOnOpen((s)->{
            if(s.path().startsWith("/xxx/")) {
                s.pathNew(s.path().substring(5));
            }
        });
    }
}
