package com.dtflys.forest.solon;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.config.ForestConfiguration;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {

        ForestConfiguration configuration = Forest.config();

        //properties
        Map<String,Object> props=new HashMap<>();
        for(Map.Entry<Object,Object> entry: Solon.cfg().entrySet()){
            props.put(entry.getKey().toString(),entry.getKey());
        }
        configuration.setVariables(props);

        configuration.setInterceptors(Arrays.asList(UpstreamInterceptor.class));//.add();
        context.beanBuilderAdd(ForestClient.class, (clz, wrap, anno) -> {
            getProxy(clz, anno, obj -> wrap.context().wrapAndPut(clz, obj));
        });
    }

    private void getProxy(Class<?> clz, ForestClient anno, Consumer consumer) {
        Object client = Forest.client(clz);

        consumer.accept(client);
    }
}
