package com.dtflys.forest.solon.integration;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.solon.UpstreamInterceptor;
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

        ForestConfiguration configuration = Forest.config();

        //1.properties
        Map<String, Object> props = new HashMap<>();
        for (Map.Entry<Object, Object> entry : Solon.cfg().entrySet()) {
            props.put(entry.getKey().toString(), entry.getKey());
        }
        configuration.setVariables(props);

        //2.interceptors
        configuration.setInterceptors(Arrays.asList(UpstreamInterceptor.class));//.add();

        //3.推进容器，便于进行定制
        context.wrapAndPut(ForestConfiguration.class, configuration);

        //4.添加注解支持
        context.beanBuilderAdd(ForestClient.class, (clz, wrap, anno) -> {
            Object client = Forest.client(clz);
            wrap.context().wrapAndPut(clz, client);
        });
    }
}
