package org.noear.solon.extend.graphql.config;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.event.AppBeanLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.extend.graphql.execution.DefaultGraphQlSource;
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
@Configuration
public class GraphqlConfiguration {

    private static Logger log = LoggerFactory.getLogger(GraphqlConfiguration.class);

    public GraphqlConfiguration() {
    }

    /**
     * 注入GraphQlSource
     */
    @Bean
    public GraphQlSource defaultGraphqlSource() {
        GraphqlResourceResolverCollect graphqlResourceResolverCollect = new GraphqlResourceResolverCollect();
        EventBus.push(graphqlResourceResolverCollect);
        RuntimeWiringConfigurerCollect runtimeWiringConfigurerCollect = new RuntimeWiringConfigurerCollect();
        EventBus.push(runtimeWiringConfigurerCollect);

        final DefaultGraphQlSource defaultGraphQlSource = new DefaultGraphQlSource();
        EventBus.subscribe(AppBeanLoadEndEvent.class, (e) -> {
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
            defaultGraphQlSource.init(graphql, graphQlSchema);
        });

        log.debug("注册默认的 GraphQlSource");
        return defaultGraphQlSource;
    }

}
