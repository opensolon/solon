package org.noear.solon.plugin.graphql;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.context().beanOnloaded(() -> {
            Aop.context().beanForeach(bw -> {
                if (GraphQLQueryResolver.class.isAssignableFrom(bw.clz())) {

                }
            });
        });
    }
}
