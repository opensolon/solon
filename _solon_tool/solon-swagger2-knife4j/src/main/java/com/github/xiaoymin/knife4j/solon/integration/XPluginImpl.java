package com.github.xiaoymin.knife4j.solon.integration;

import org.noear.solon.docs.annotation.EnableSwagger2;

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
        if (Solon.app().source().isAnnotationPresent(EnableSwagger2.class) == false) {
            return;
        }

        StaticMappings.add("/", new ClassPathStaticRepository("META-INF/resources"));
        //Solon.app().get("/swagger", c -> c.forward("/doc.html"));
    }
}
