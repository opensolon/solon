package net.hasor.solon.boot;

import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.InitializingBean;

import java.io.IOException;

/**
 * 将注解的配置转到 BuildConfig 实例上
 *
 * @author noear
 * @since 2020.10.10
 * */
@Configuration
public class HasorConfiguration implements InitializingBean {
    @Inject
    private org.noear.solon.core.AppContext context;

    /**
     * 此构建函数，是为了手动写代码提供支持；充许EnableHasor注在别的临时类上实现配置
     * <p>
     * 为开发隐式插件提供支持
     */
    @Override
    public void afterInjection() throws Throwable {
        EnableHasor enableHasor = Solon.app().source().getAnnotation(EnableHasor.class);

        BuildConfig buildConfig = BuildConfig.getInstance();

        // 处理mainConfig
        buildConfig.mainConfig = enableHasor.mainConfig();

        // 处理useProperties
        buildConfig.useProperties = enableHasor.useProperties();

        // 处理startWith
        for (Class<? extends Module> startWith : enableHasor.startWith()) {
            if(startWith.getAnnotations().length > 0) {
                context.getWrapAsync(startWith, (bw) -> {
                    buildConfig.addModules(bw.get());
                });
            }else{
                buildConfig.addModules(context.getBeanOrNew(startWith));
            }
        }

        // 把Solon 中所有标记了 @DimModule 的 Module，捞进来。 //交给XPluginImp处理

        //
        // 处理scanPackages
        if (enableHasor.scanPackages().length != 0) {
            for (String p : enableHasor.scanPackages()) {
                if (p.endsWith(".*")) {
                    context.beanScan(p.substring(0, p.length() - 2));
                } else {
                    context.beanScan(p);
                }
            }
        }

        // 处理customProperties
        Property[] customProperties = enableHasor.customProperties();
        for (Property property : customProperties) {
            String name = property.name();
            if (StringUtils.isNotBlank(name)) {
                buildConfig.customProperties.put(name, property.value());
            }
        }

        //没有EnableHasorWeb时，生成AppContext并注入容器
        //
        if (Solon.app().source().getAnnotation(EnableHasorWeb.class) == null) {
            //所有bean加载完成之后，手动注入AppContext
            context.wrapAndPut(AppContext.class, initAppContext());
        }
    }

    private AppContext initAppContext() {
        try {
            return BuildConfig.getInstance().build(null);
        } catch (IOException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}
