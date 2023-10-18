package labs.importTest.case2;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Import;

/**
 * @author noear 2023/10/18 created
 */
@Import(classes = DemoConfig.class, propertySource = "classpath:demo.properties")
@Configuration
public class AutoConfig {
}
