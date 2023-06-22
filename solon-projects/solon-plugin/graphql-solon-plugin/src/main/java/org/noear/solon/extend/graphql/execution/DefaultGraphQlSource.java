package org.noear.solon.extend.graphql.execution;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class DefaultGraphQlSource implements GraphQlSource {

    private final GraphQL graphQl;

    private final GraphQLSchema schema;

    public DefaultGraphQlSource(GraphQL graphQl, GraphQLSchema schema) {
        this.graphQl = graphQl;
        this.schema = schema;
    }

    @Override
    public GraphQL graphQl() {
        return this.graphQl;
    }

    @Override
    public GraphQLSchema schema() {
        return this.schema;
    }

}
