package com.dtflys.forest.solon.integration;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.SolonForestProperties;
import com.dtflys.forest.file.SolonMultipartFile;
import com.dtflys.forest.http.body.SolonUploadRequestBodyBuilder;
import com.dtflys.forest.http.body.RequestBodyBuilder;
import com.dtflys.forest.interceptor.SolonInterceptorFactory;
import com.dtflys.forest.multipart.ForestMultipartFactory;
import com.dtflys.forest.reflection.SolonObjectFactory;
import com.dtflys.forest.solon.ForestBeanBuilder;
import com.dtflys.forest.solon.SolonForestVariableValue;
import com.dtflys.forest.solon.SolonUpstreamInterceptor;
import com.dtflys.forest.solon.properties.ForestConfigurationProperties;
import com.dtflys.forest.utils.StringUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.handle.UploadedFile;

import java.util.Arrays;

/**
 * @author 夜の孤城
 * @since 1.10
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        //1.初始 ForestConfiguration
        ForestConfiguration configuration = configBeanInit(context);

        //2.注册上传类型
        ForestMultipartFactory.registerFactory(UploadedFile.class, SolonMultipartFile.class);
        RequestBodyBuilder.registerBodyBuilder(UploadedFile.class, new SolonUploadRequestBodyBuilder());

        //3.添加 ForestClient 注解支持
        context.beanBuilderAdd(ForestClient.class, (clz, wrap, anno) -> {
            Object client = configuration.client(clz);
            context.wrapAndPut(clz, client);
        });

        //4.添加 BindingVar 注解支持
        context.beanExtractorAdd(BindingVar.class, (bw, method, anno) -> {
            String confId = anno.configuration();
            ForestConfiguration config = null;

            if (StringUtils.isNotBlank(confId)) {
                config = Forest.config(confId);
            } else {
                config = configuration;
            }

            String varName = anno.value();
            SolonForestVariableValue variableValue = new SolonForestVariableValue(bw.get(), method);
            config.setVariableValue(varName, variableValue);
        });


    }

    private ForestConfiguration configBeanInit(AppContext context) {
        Props forestProps = context.cfg().getProp("forest");
        ForestConfigurationProperties configurationProperties = new ForestConfigurationProperties();
        Utils.injectProperties(configurationProperties, forestProps);

        ForestBeanBuilder forestBeanBuilder = new ForestBeanBuilder(
                configurationProperties,
                new SolonForestProperties(context),
                new SolonObjectFactory(context),
                new SolonInterceptorFactory(context));

        //1.构建配转走
        ForestConfiguration config = forestBeanBuilder.build();

        //2.添加必要拦截器
        if (config.getInterceptors() != null) {
            config.getInterceptors().add(SolonUpstreamInterceptor.class);
        } else {
            config.setInterceptors(Arrays.asList(SolonUpstreamInterceptor.class));
        }

        //3.注册到容器
        String beanId = configurationProperties.getBeanId();
        if (Utils.isEmpty(beanId)) {
            beanId = "forestConfiguration";
        }

        BeanWrap beanWrap = context.wrap(beanId, config);
        context.putWrap(ForestConfiguration.class, beanWrap);
        context.putWrap(beanId, beanWrap);

        return config;
    }
}
