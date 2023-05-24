package com.github.xiaoymin.knife4j.solon.integration;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
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
        if (Solon.app().enableDoc() == false) {
            return;
        }

        if (context.hasWrap(OpenApiExtensionResolver.class) == false) {
            context.beanMake(OpenApiExtensionResolver.class);
        }

        StaticMappings.add("/", new ClassPathStaticRepository("META-INF/resources"));
    }
}
