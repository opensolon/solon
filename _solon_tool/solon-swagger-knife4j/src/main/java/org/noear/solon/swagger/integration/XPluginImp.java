package org.noear.solon.swagger.integration;

import java.util.Properties;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Props;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;
import org.noear.solon.swagger.SwaggerConfig;
import org.noear.solon.swagger.annotation.EnableSwagger;
import org.noear.solon.swagger.SwaggerConst;
import org.noear.solon.core.Plugin;

/**
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/6/12
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        if (Solon.app().source().isAnnotationPresent(EnableSwagger.class) == false) {
            return;
        }

        StaticMappings.add("/", new ClassPathStaticRepository("META-INF/resources"));
        Solon.app().add("", SwaggerController.class);


        context.beanOnloaded((ctx)->{
            SwaggerConfig config = ctx.getBeanOrNew(SwaggerConfig.class);

            Properties properties = Utils.loadProperties(config.getPropPath());

            SwaggerConst.CONFIG = new Props();
            SwaggerConst.CONFIG.putAll(properties);
            SwaggerConst.HTTP_CODE = config.getHttpCode();
            SwaggerConst.COMMON_RES = config.getCommonRet();
            SwaggerConst.RESPONSE_IN_DATA = SwaggerConst.CONFIG.getBool("responseInData", true);

        });
    }
}
