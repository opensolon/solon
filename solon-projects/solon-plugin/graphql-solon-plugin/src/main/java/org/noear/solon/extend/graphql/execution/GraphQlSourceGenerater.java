package org.noear.solon.extend.graphql.execution;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.noear.solon.extend.graphql.support.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public abstract class GraphQlSourceGenerater {

    private static Logger log = LoggerFactory.getLogger(GraphQlSourceGenerater.class);

    public static GraphQlSource generateGraphqlSource(Set<Resource> resourceSet,
            List<RuntimeWiringConfigurer> configurers) {
        DefaultSchemaResourceGraphQlSourceBuilder defaultBuilder = new DefaultSchemaResourceGraphQlSourceBuilder();
        defaultBuilder.schemaResources(resourceSet);

        if (Objects.nonNull(configurers)) {
            configurers.forEach(defaultBuilder::configureRuntimeWiring);
        }

        GraphQLSchema graphQlSchema = defaultBuilder.getGraphQlSchema();
        GraphQL graphql = GraphQL.newGraphQL(graphQlSchema).build();

        log.debug("生成 GraphQlSource");
        return new DefaultGraphQlSource(graphql, graphQlSchema);
    }

}
