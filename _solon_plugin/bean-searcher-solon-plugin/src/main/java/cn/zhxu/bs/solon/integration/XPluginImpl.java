package cn.zhxu.bs.solon.integration;


import cn.zhxu.bs.solon.BeanSearcherAutoConfiguration;
import cn.zhxu.bs.solon.BeanSearcherAutoConfigurationBef;
import cn.zhxu.bs.solon.BeanSearcherProperties;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.1
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        context.beanMake(BeanSearcherProperties.class);

        //容器加载完成后再执行，确保用户的 Bean 优先

        //先处理没有 getBeansOfType 接口调用的
        context.lifecycle(-99, () -> {
            context.beanMake(BeanSearcherAutoConfigurationBef.class);
        });


        //再处理有 getBeansOfType 接口调用的
        context.lifecycle(-98, () -> {
            context.beanMake(BeanSearcherAutoConfiguration.class);
        });
    }
}
