/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.extend.graphql.execution;

import graphql.GraphQLContext;
import org.dataloader.DataLoaderRegistry;

/**
 * copy from org.springframework.graphql.execution.DataLoaderRegistrar
 *
 * Contract for access to the {@link DataLoaderRegistry} for each request for
 * the purpose of registering {@link org.dataloader.DataLoader} instances.
 *
 * @author Rossen Stoyanchev
 * @see ExecutionInput#getDataLoaderRegistry()
 * @since 1.0.0
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
