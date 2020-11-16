package org.noear.solon.plugin.graphql;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.noear.solon.Solon;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(Solon app) {
        Aop.context().beanOnloaded(() -> {
            Aop.context().beanForeach(bw -> {
                if (GraphQLQueryResolver.class.isAssignableFrom(bw.clz())) {

                }
            });
        });
    }
}
