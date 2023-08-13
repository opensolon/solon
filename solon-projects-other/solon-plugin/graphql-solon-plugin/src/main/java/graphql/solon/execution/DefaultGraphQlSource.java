package graphql.solon.execution;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class DefaultGraphQlSource implements GraphQlSource {

    private Boolean initFlag;
    private GraphQL graphQl;
    private GraphQLSchema schema;

    public DefaultGraphQlSource() {
        this.initFlag = false;
    }

    @Override
    public void init(GraphQL graphQl, GraphQLSchema schema) {
        this.graphQl = graphQl;
        this.schema = schema;
        this.initFlag = true;
    }

    @Override
    public GraphQL graphQl() {
        if (!this.initFlag) {
            throw new IllegalStateException("un init");
        }
        return this.graphQl;
    }

    @Override
    public GraphQLSchema schema() {
        if (!this.initFlag) {
            throw new IllegalStateException("un init");
        }
        return this.schema;
    }
}
