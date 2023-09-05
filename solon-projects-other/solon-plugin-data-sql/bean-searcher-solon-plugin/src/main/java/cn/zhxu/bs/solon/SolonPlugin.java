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

        //容器加载完成后再执行，确保用户的 Bean 优先
        // 先处理没有 getBeansOfType 接口调用的
        context.lifecycle(-99, () -> context.beanMake(ConfigurationBefore.class));

        //再处理有 getBeansOfType 接口调用的
        context.lifecycle(-98, () -> context.beanMake(ConfigurationAfter.class));
    }

}
