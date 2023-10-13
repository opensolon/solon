package cn.zhxu.bs.solon;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.1
 */
public class SolonPlugin implements Plugin {

    @Override
    public void start(AppContext context) {
        context.beanMake(BeanSearcherProperties.class);
        context.beanMake(BeanSearcherConfiguration.class);
    }
}
