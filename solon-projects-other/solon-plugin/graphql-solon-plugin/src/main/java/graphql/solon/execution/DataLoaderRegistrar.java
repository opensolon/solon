package graphql.solon.execution;

import graphql.GraphQLContext;
import org.dataloader.DataLoaderRegistry;

/**
 * @author fuzi1996
 * @since 2.3
 */
public interface DataLoaderRegistrar {

    /**
     * Callback that provides access to the {@link DataLoaderRegistry} from the
     * the {@link graphql.ExecutionInput}.
     *
     * @param registry the registry to make registrations against
     * @param context the GraphQLContext from the ExecutionInput that registrars
     * should set in the {@link org.dataloader.DataLoaderOptions} so that batch
     * loaders can access it via {@link org.dataloader.BatchLoaderEnvironment}.
     */
    void registerDataLoaders(DataLoaderRegistry registry, GraphQLContext context);

}
