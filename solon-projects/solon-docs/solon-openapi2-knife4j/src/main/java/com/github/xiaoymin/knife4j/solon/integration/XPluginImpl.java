package com.github.xiaoymin.knife4j.solon.integration;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

/**
 * @author noear
 * @since 2.2
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        if (Solon.app().enableDoc() == false) {
            return;
        }

        BeanWrap beanWrap = context.beanMake(OpenApiExtensionResolver.class);
        OpenApiExtensionResolver openApiExtensionResolver = beanWrap.raw();

        if (openApiExtensionResolver.getSetting().isEnable()) {
            String uiPath = "/";
            if (openApiExtensionResolver.getSetting().isProduction()) {
                if (Solon.cfg().isFilesMode() == false) {
                    //生产环境，只有 files 模式才能用（即开发模式）
                    StaticMappings.add(uiPath, new ClassPathStaticRepository("META-INF/resources"));
                }
            } else {
                //非生产环境，一直可用
                StaticMappings.add(uiPath, new ClassPathStaticRepository("META-INF/resources"));
            }

            Solon.app().add(uiPath, Swagger2Controller.class);
        }
    }
}
