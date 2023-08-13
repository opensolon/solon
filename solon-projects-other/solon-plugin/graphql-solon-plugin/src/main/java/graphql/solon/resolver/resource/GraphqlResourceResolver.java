package graphql.solon.resolver.resource;

import java.util.Set;
import graphql.solon.resource.Resource;

/**
 * @author fuzi1996
 * @since 2.3
 */
public interface GraphqlResourceResolver {

    default boolean isNeedAppend(Set<Resource> existsResource) {
        return true;
    }

    Set<Resource> getGraphqlResource();
}
