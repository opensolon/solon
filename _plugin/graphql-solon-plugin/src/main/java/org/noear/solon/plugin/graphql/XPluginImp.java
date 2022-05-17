package org.noear.solon.plugin.graphql;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanOnloaded((ctx) -> {
            ctx.beanForeach(bw -> {
                if (GraphQLQueryResolver.class.isAssignableFrom(bw.clz())) {

                }
            });
        });
    }
}
