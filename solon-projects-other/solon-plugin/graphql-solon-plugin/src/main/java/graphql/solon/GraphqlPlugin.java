package graphql.solon;

import graphql.solon.annotation.BatchMapping;
import graphql.solon.annotation.BatchMappingAnnoHandler;
import graphql.solon.annotation.QueryMapping;
import graphql.solon.annotation.QueryMappingAnnoHandler;
import graphql.solon.annotation.SchemaMapping;
import graphql.solon.annotation.SchemaMappingAnnoHandler;
import graphql.solon.config.GraphqlConfiguration;
import graphql.solon.controller.GraphqlController;
import graphql.solon.event.AppLoadEndEventListener;
import graphql.solon.event.DefaultCprResolverEventListener;
import graphql.solon.event.DefaultRwConfigurerCollectEventListener;
import graphql.solon.execution.BatchLoaderRegistry;
import graphql.solon.execution.DefaultBatchLoaderRegistry;
import graphql.solon.properties.GraphqlProperties;
import graphql.solon.resolver.argument.HandlerMethodArgumentResolver;
import graphql.solon.resolver.argument.HandlerMethodArgumentResolverCollect;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class GraphqlPlugin implements Plugin {

    private static Logger log = LoggerFactory.getLogger(GraphqlPlugin.class);

    @Override
    public void start(AppContext context) {
        log.debug("load GraphqlPlugin ...");

        HandlerMethodArgumentResolverCollect methodArgumentResolverCollect = new HandlerMethodArgumentResolverCollect();
        context.wrapAndPut(HandlerMethodArgumentResolverCollect.class,
                methodArgumentResolverCollect);
        context.getBeanAsync(HandlerMethodArgumentResolver.class,
                methodArgumentResolverCollect::append);

        BatchLoaderRegistry defaultBatchLoaderRegistry = new DefaultBatchLoaderRegistry();
        context.wrapAndPut(BatchLoaderRegistry.class, defaultBatchLoaderRegistry);

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
