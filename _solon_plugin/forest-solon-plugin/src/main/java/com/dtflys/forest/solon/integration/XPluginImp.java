package com.dtflys.forest.solon.integration;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.SolonForestProperties;
import com.dtflys.forest.interceptor.SolonInterceptorFactory;
import com.dtflys.forest.reflection.SolonObjectFactory;
import com.dtflys.forest.solon.ForestBeanBuilder;
import com.dtflys.forest.solon.SolonForestVariableValue;
import com.dtflys.forest.solon.SolonUpstreamInterceptor;
import com.dtflys.forest.solon.properties.ForestConfigurationProperties;
import com.dtflys.forest.utils.StringUtils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

import java.util.Arrays;

/**
 * @author 夜の孤城
 * @since 1.10
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {

        //1.初始 ForestConfiguration
        configBeanInit(context);

        //2.添加 ForestClient 注解支持
        context.beanBuilderAdd(ForestClient.class, (clz, wrap, anno) -> {
            Object client = Forest.client(clz);
            wrap.context().wrapAndPut(clz, client);
        });

        //3.添加 BindingVar 注解支持
        context.beanExtractorAdd(BindingVar.class, (bw, method, anno) -> {
            String confId = anno.configuration();
            ForestConfiguration configuration = null;

            if (StringUtils.isNotBlank(confId)) {
                configuration = Forest.config(confId);
            } else {
                configuration = Forest.config();
            }

            String varName = anno.value();
            SolonForestVariableValue variableValue = new SolonForestVariableValue(bw.get(), method);
            configuration.setVariableValue(varName, variableValue);
        });
    }

    private void configBeanInit(AopContext context) {
        ForestConfigurationProperties configurationProperties = context.beanMake(ForestConfigurationProperties.class).get();

        ForestBeanBuilder forestBeanBuilder = new ForestBeanBuilder(context,
                configurationProperties,
                new SolonForestProperties(context),
                new SolonObjectFactory(context),
                new SolonInterceptorFactory(context));

        //1.构建配转走
        ForestConfiguration config = forestBeanBuilder.build();

        //2.添加必要拦截器
        config.setInterceptors(Arrays.asList(SolonUpstreamInterceptor.class));//.add();
    }
}
