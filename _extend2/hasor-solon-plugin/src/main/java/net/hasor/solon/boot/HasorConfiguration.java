package net.hasor.solon.boot;

import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.solon.beans.AutoScanPackagesModule;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import org.noear.solon.XApp;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.core.Aop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 将注解的配置转到 BuildConfig 实例上
 *
 * @author noear
 * @since 2020.10.10
 * */
@XConfiguration
public class HasorConfiguration {
    private static Logger logger = LoggerFactory.getLogger(HasorConfiguration.class);

    public HasorConfiguration() {
        this(XApp.global().source().getAnnotation(EnableHasor.class));
    }

    /**
     * 此构建函数，是为了手动写代码提供支持；充许EnableHasor注在别的临时类上实现配置
     * <p>
     * 为开发隐式插件提供支持
     */
    public HasorConfiguration(EnableHasor enableHasor) {
        BuildConfig buildConfig = BuildConfig.getInstance();

        // 处理mainConfig
        buildConfig.mainConfig = enableHasor.mainConfig();

        // 处理useProperties
        buildConfig.useProperties = enableHasor.useProperties();

        // 处理startWith
        for (Class<? extends Module> startWith : enableHasor.startWith()) {
            buildConfig.loadModules.add(Aop.get(startWith));
        }

        buildConfig.needCheckRepeat.addAll(Arrays.asList(enableHasor.startWith()));
        // 把Solon 中所有标记了 @DimModule 的 Module，捞进来。 //交给XPluginImp处理

        //
        // 处理scanPackages
        if (enableHasor.scanPackages().length != 0) {
            AutoScanPackagesModule autoScanModule = new AutoScanPackagesModule(
                    enableHasor.scanPackages(),
                    Matchers.anyClassExcludes(buildConfig.needCheckRepeat));
            buildConfig.loadModules.add(autoScanModule);
        }

        // 处理customProperties
        Property[] customProperties = enableHasor.customProperties();
        for (Property property : customProperties) {
            String name = property.name();
            if (StringUtils.isNotBlank(name)) {
                buildConfig.customProperties.put(name, property.value());
            }
        }

        //
        // .打印 Hello
        printLogo();

        //将AppContext注入容器
        //
        if (XApp.global().source().getAnnotation(EnableHasorWeb.class) == null) {
            Aop.wrapAndPut(AppContext.class, initAppContext());
        }
    }

    private AppContext initAppContext() {
        try {
            return BuildConfig.getInstance().build(null).build();
        } catch (IOException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    private void printLogo() {
        try {
            InputStream inputStream = ResourcesUtils.getResourceAsStream("/META-INF/hasor-framework/hasor-spring-hello.txt");
            List<String> helloText = IOUtils.readLines(inputStream, "utf-8");
            StringBuilder builder = new StringBuilder("\n");
            for (String msg : helloText) {
                builder.append(msg).append("\n");
            }
            logger.info(builder.toString());
        } catch (Exception e) { /**/ }
    }
}
