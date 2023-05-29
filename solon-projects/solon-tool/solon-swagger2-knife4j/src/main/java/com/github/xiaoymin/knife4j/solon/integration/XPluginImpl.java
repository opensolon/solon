package com.github.xiaoymin.knife4j.solon.integration;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
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
    public void start(AopContext context) throws Throwable {
        BeanWrap beanWrap = context.beanMake(OpenApiExtensionResolver.class);
        OpenApiExtensionResolver openApiExtensionResolver = beanWrap.raw();

        if (openApiExtensionResolver.getSetting().isEnable()) {
            if (openApiExtensionResolver.getSetting().isProduction()) {
                if (Solon.cfg().isFilesMode() == false) {
                    //生产环境，只有 files 模式才能用（即开发模式）
                    Solon.app().enableDoc(true);
                    StaticMappings.add("/", new ClassPathStaticRepository("META-INF/resources"));
                    return;
                }
            } else {
                //非生产环境，一直可用
                Solon.app().enableDoc(true);
                StaticMappings.add("/", new ClassPathStaticRepository("META-INF/resources"));
                return;
            }
        }

        Solon.app().enableDoc(false);
    }
}
