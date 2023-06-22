package org.noear.solon.extend.graphql.execution;

import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.WiringFactory;
import java.util.List;

/**
 * @author fuzi1996
 * @since 2.3
 */
public interface RuntimeWiringConfigurer {

    void configure(RuntimeWiring.Builder builder);

    default void configure(RuntimeWiring.Builder builder, List<WiringFactory> container) {
        // no-op
    }
}
