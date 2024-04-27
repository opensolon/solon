package demo;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.properties.PropertiesActionExecutor;

/**
 * @author noear
 * @since 2.7
 */
@Configuration
public class DemoConfig {
    public void xxx(PropertiesActionExecutor actionExecutor){
        actionExecutor.includeFormUrlencoded(true);
    }
}
