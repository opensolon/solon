package graphql.solon.execution;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.TypeRuntimeWiring;
import graphql.solon.configurer.RuntimeWiringConfigurer;
import graphql.solon.resource.ByteArrayResource;
import graphql.solon.resource.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * 测试graphql资源加载
 *
 * @author fuzi1996
 * @since 2.3
 */
public class GraphQlSourceTest {

    private GraphQlSource getGraphqlSource(Set<Resource> resources,
            List<RuntimeWiringConfigurer> configurers) {
        DefaultSchemaResourceGraphQlSourceBuilder defaultBuilder = new DefaultSchemaResourceGraphQlSourceBuilder();
        defaultBuilder.schemaResources(resources);

        if (Objects.nonNull(configurers)) {
            configurers.forEach(defaultBuilder::configureRuntimeWiring);
        }

        GraphQLSchema graphQlSchema = defaultBuilder.getGraphQlSchema();
        GraphQL graphql = GraphQL.newGraphQL(graphQlSchema).build();
        DefaultGraphQlSource defaultGraphQlSource = new DefaultGraphQlSource();
        defaultGraphQlSource.init(graphql, graphQlSchema, null);
        return defaultGraphQlSource;
    }

    @Test
    void graphQl() {
        RuntimeWiringConfigurer configurer =
                (builder) -> builder.type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("hello", new StaticDataFetcher("world")));

        String schema = "type Query{hello: String}";
        ByteArrayResource resource = new ByteArrayResource(schema);

        GraphQlSource graphQlSource = this.getGraphqlSource(Collections.singleton(resource),
                Collections.singletonList(configurer));
        GraphQL graphQL = graphQlSource.graphQl();
        ExecutionResult result = graphQL.execute("{hello}");
        Object data = result.getData();
        assertThat(data, instanceOf(Map.class));
        assertThat((Map<?, ?>) data, aMapWithSize(1));
        assertThat((Map<?, ?>) data, hasEntry(is("hello"), is("world")));
    }
}