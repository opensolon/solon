package webapp.models;

import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2022/12/10 created
 */
@Condition(onMissingBean = CfgItem.class)
@Configuration
public class BaseRequest {
}
