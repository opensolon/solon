package org.noear.solon.extend.graphql.execution;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.TypeRuntimeWiring;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.noear.solon.extend.graphql.support.ByteArrayResource;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class GraphQlSourceTest {

    @Test
    void graphQl() {
        RuntimeWiringConfigurer configurer =
                (builder) -> builder.type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("hello", new StaticDataFetcher("world")));

        String schema = "type Query{hello: String}";
        ByteArrayResource resource = new ByteArrayResource(schema);
        GraphQlSource graphQlSource = GraphQlSourceGenerater
                .generateGraphqlSource(Collections.singleton(resource),
                        Collections.singletonList(configurer));
        GraphQL graphQL = graphQlSource.graphQl();
        ExecutionResult result = graphQL.execute("{hello}");
        Object data = result.getData();
        assertThat(data, instanceOf(Map.class));
        assertThat((Map<?, ?>) data, aMapWithSize(1));
        assertThat((Map<?, ?>) data, hasEntry(is("hello"), is("world")));
    }
}