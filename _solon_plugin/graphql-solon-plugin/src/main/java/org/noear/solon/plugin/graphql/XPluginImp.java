package org.noear.solon.plugin.graphql;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        context.beanOnloaded((ctx) -> {
            ctx.beanForeach(bw -> {
                if (GraphQLQueryResolver.class.isAssignableFrom(bw.clz())) {

                }
            });
        });
    }
}
