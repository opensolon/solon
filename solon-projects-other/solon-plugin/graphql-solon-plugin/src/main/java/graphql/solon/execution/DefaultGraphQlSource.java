package graphql.solon.execution;

import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.GraphQLContext;
import graphql.schema.GraphQLSchema;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.dataloader.DataLoaderRegistry;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class DefaultGraphQlSource implements GraphQlSource {

    private Boolean initFlag;
    private GraphQL graphQl;
    private GraphQLSchema schema;
    private List<DataLoaderRegistrar> dataLoaderRegistrars;

    public DefaultGraphQlSource() {
        this.initFlag = false;
    }

    @Override
    public void init(GraphQL graphQl, GraphQLSchema schema,
            List<DataLoaderRegistrar> dataLoaderRegistrars) {
        this.graphQl = graphQl;
        this.schema = schema;
        if (Objects.isNull(dataLoaderRegistrars)) {
            this.dataLoaderRegistrars = Collections.emptyList();
        } else {
            this.dataLoaderRegistrars = dataLoaderRegistrars;
        }
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

    @Override
    public ExecutionInput registerDataLoaders(ExecutionInput executionInput) {
        if (!this.dataLoaderRegistrars.isEmpty()) {
            GraphQLContext graphQLContext = executionInput.getGraphQLContext();
            DataLoaderRegistry previousRegistry = executionInput.getDataLoaderRegistry();
            DataLoaderRegistry newRegistry = DataLoaderRegistry.newRegistry()
                    .registerAll(previousRegistry).build();
            this.dataLoaderRegistrars.forEach(
                    registrar -> registrar.registerDataLoaders(newRegistry, graphQLContext));
            return executionInput
                    .transform(builder -> builder.dataLoaderRegistry(newRegistry));
        }
        return executionInput;
    }
}
