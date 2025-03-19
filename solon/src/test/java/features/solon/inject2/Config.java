package features.solon.inject2;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/3/19 created
 */
@Configuration
public class Config {
    @Inject
    Map<String, DnBean> dnBeanMap;

    @Inject
    List<DsBean> dsBeanList;
}
