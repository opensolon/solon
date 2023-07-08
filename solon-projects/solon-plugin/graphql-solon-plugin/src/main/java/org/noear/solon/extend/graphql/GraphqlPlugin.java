package org.noear.solon.extend.graphql;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.extend.graphql.annotation.QueryMapping;
import org.noear.solon.extend.graphql.annotation.QueryMappingAnnoHandler;
import org.noear.solon.extend.graphql.config.GraphqlConfiguration;
import org.noear.solon.extend.graphql.controller.GraphqlController;
import org.noear.solon.extend.graphql.event.AppLoadEndEventListener;
import org.noear.solon.extend.graphql.event.DefaultCprResolverEventListener;
import org.noear.solon.extend.graphql.event.DefaultRwConfigurerCollectEventListener;
import org.noear.solon.extend.graphql.properties.GraphqlProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class GraphqlPlugin implements Plugin {

    private static Logger log = LoggerFactory.getLogger(GraphqlPlugin.class);

    @Override
    public void start(AopContext context) {
        log.debug("load GraphqlPlugin ...");

        QueryMappingAnnoHandler extractor = new QueryMappingAnnoHandler(context);
        context.beanExtractorAdd(QueryMapping.class, extractor);

        context.wrapAndPut(QueryMappingAnnoHandler.class, extractor);

        context.lifecycle(-99, () -> {
            context.beanMake(GraphqlProperties.class);
            context.beanMake(DefaultCprResolverEventListener.class);
            context.beanMake(DefaultRwConfigurerCollectEventListener.class);
            context.beanMake(GraphqlConfiguration.class);
            context.beanMake(GraphqlController.class);
        });

        EventBus.subscribe(AppLoadEndEvent.class, new AppLoadEndEventListener());
    }
}
