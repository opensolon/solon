package org.noear.solon.extend.graphql;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.extend.graphql.annotation.BatchMapping;
import org.noear.solon.extend.graphql.annotation.BatchMappingAnnoHandler;
import org.noear.solon.extend.graphql.annotation.QueryMapping;
import org.noear.solon.extend.graphql.annotation.QueryMappingAnnoHandler;
import org.noear.solon.extend.graphql.annotation.SchemaMapping;
import org.noear.solon.extend.graphql.annotation.SchemaMappingAnnoHandler;
import org.noear.solon.extend.graphql.config.GraphqlConfiguration;
import org.noear.solon.extend.graphql.controller.GraphqlController;
import org.noear.solon.extend.graphql.event.AppLoadEndEventListener;
import org.noear.solon.extend.graphql.event.DefaultCprResolverEventListener;
import org.noear.solon.extend.graphql.event.DefaultRwConfigurerCollectEventListener;
import org.noear.solon.extend.graphql.properties.GraphqlProperties;
import org.noear.solon.extend.graphql.resolver.argument.HandlerMethodArgumentResolver;
import org.noear.solon.extend.graphql.resolver.argument.HandlerMethodArgumentResolverCollect;
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

        HandlerMethodArgumentResolverCollect methodArgumentResolverCollect = new HandlerMethodArgumentResolverCollect();
        context.wrapAndPut(HandlerMethodArgumentResolverCollect.class,
                methodArgumentResolverCollect);
        context.getBeanAsync(HandlerMethodArgumentResolver.class,
                methodArgumentResolverCollect::append);

        SchemaMappingAnnoHandler schemaExtractor = new SchemaMappingAnnoHandler(context);
        context.beanExtractorAdd(SchemaMapping.class, schemaExtractor);

        QueryMappingAnnoHandler queryExtractor = new QueryMappingAnnoHandler(context);
        context.beanExtractorAdd(QueryMapping.class, queryExtractor);

        BatchMappingAnnoHandler batchMappingExtractor = new BatchMappingAnnoHandler(context);
        context.beanExtractorAdd(BatchMapping.class, batchMappingExtractor);

        context.wrapAndPut(QueryMappingAnnoHandler.class, queryExtractor);
        context.wrapAndPut(SchemaMappingAnnoHandler.class, schemaExtractor);
        context.wrapAndPut(BatchMappingAnnoHandler.class, batchMappingExtractor);

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
