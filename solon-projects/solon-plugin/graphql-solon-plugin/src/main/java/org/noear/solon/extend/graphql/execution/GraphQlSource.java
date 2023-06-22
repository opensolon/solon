package org.noear.solon.extend.graphql.execution;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author fuzi1996
 * @since 2.3
 */
public interface GraphQlSource {


    /**
     * Return the {@link GraphQL} to use. This can be a cached instance or a different one from time
     * to time (e.g. based on a reloaded schema).
     */
    GraphQL graphQl();

    /**
     * Return the {@link GraphQLSchema} used by the current {@link GraphQL}.
     */
    GraphQLSchema schema();

}

