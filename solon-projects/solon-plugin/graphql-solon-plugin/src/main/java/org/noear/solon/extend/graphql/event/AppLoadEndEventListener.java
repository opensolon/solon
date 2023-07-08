package org.noear.solon.extend.graphql.event;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.extend.graphql.GraphqlPlugin;
import org.noear.solon.extend.graphql.execution.DefaultSchemaResourceGraphQlSourceBuilder;
import org.noear.solon.extend.graphql.execution.GraphQlSource;
import org.noear.solon.extend.graphql.execution.collect.GraphqlResourceResolverCollect;
import org.noear.solon.extend.graphql.execution.collect.RuntimeWiringConfigurerCollect;
import org.noear.solon.extend.graphql.execution.configurer.RuntimeWiringConfigurer;
import org.noear.solon.extend.graphql.execution.resolver.GraphqlResourceResolver;
import org.noear.solon.extend.graphql.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class AppLoadEndEventListener implements EventListener<AppLoadEndEvent> {

    private static Logger log = LoggerFactory.getLogger(GraphqlPlugin.class);

    @Override
    public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
        GraphqlResourceResolverCollect graphqlResourceResolverCollect = new GraphqlResourceResolverCollect();
        RuntimeWiringConfigurerCollect runtimeWiringConfigurerCollect = new RuntimeWiringConfigurerCollect();

        EventBus.push(graphqlResourceResolverCollect);
        EventBus.push(runtimeWiringConfigurerCollect);

        AopContext aopContext = appLoadEndEvent.context();
        GraphQlSource graphQlSource = aopContext.getBean(GraphQlSource.class);
        Set<Resource> resources = new LinkedHashSet<>();
        List<GraphqlResourceResolver> resolvers = graphqlResourceResolverCollect
                .getAllCollector();
        if (Objects.nonNull(resolvers)) {
            resolvers.forEach(resolver -> {
                if (resolver.isNeedAppend(resources)) {
                    Set<Resource> otherResources = resolver.getGraphqlResource();
                    resources.addAll(otherResources);
                }
            });
        }

        List<RuntimeWiringConfigurer> configurers = runtimeWiringConfigurerCollect
                .getAllCollector();

        DefaultSchemaResourceGraphQlSourceBuilder defaultBuilder = new DefaultSchemaResourceGraphQlSourceBuilder();
        defaultBuilder.schemaResources(resources);

        if (Objects.nonNull(configurers)) {
            configurers.forEach(defaultBuilder::configureRuntimeWiring);
        }

        GraphQLSchema graphQlSchema = defaultBuilder.getGraphQlSchema();
        GraphQL graphql = GraphQL.newGraphQL(graphQlSchema).build();

        log.debug("默认的 GraphQlSource 初始化");
        graphQlSource.init(graphql, graphQlSchema);
    }
}
