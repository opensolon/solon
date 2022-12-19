package com.dtflys.forest.solon.integration;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.solon.SolonVariableValue;
import com.dtflys.forest.solon.UpstreamInterceptor;
import com.dtflys.forest.utils.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            SolonVariableValue variableValue = new SolonVariableValue(bw.get(), method);
            configuration.setVariableValue(varName, variableValue);
        });
    }

    private void configBeanInit(AopContext context) {
        ForestConfiguration config = Forest.config();

        //1.properties
        Map<String, Object> props = new HashMap<>();
        for (Map.Entry<Object, Object> entry : Solon.cfg().entrySet()) {
            props.put(entry.getKey().toString(), entry.getKey());
        }
        config.setVariables(props);

        //2.interceptors
        config.setInterceptors(Arrays.asList(UpstreamInterceptor.class));//.add();

        //3.推进容器，便于进行定制
        context.wrapAndPut(ForestConfiguration.class, config);
    }
}
