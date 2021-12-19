package com.dtflys.forest.solon;

import com.dtflys.forest.annotation.ForestClient;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

import java.util.function.Consumer;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanBuilderAdd(ForestClient.class, (clz, wrap, anno) -> {
            getProxy(clz, anno, obj -> Aop.wrapAndPut(clz, obj));
        });
    }

    private void getProxy(Class<?> clz, ForestClient anno, Consumer consumer) {

    }
}
