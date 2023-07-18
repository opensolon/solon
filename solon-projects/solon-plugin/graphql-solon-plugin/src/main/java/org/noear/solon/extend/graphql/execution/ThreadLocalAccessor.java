/*
 * Copyright 2002-2022 the original author or authors.
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

import java.util.List;
import java.util.Map;

/**
 * copy from org.springframework.graphql.execution.ThreadLocalAccessor
 *
 * Interface to be implemented to assist with the extraction of ThreadLocal
 * values at the start of GraphQL request execution, e.g. in the web layer.
 * Those values are saved in {@link graphql.ExecutionInput} and restored later
 * around the invocation of data fetchers and exception resolvers which may be
 * in a different thread.
 *
 * <p>Implementations of this interface are typically declared as beans in
 * Spring configuration and are invoked in order as defined in
 * {@link ObjectProvider#orderedStream()}.
 *
 * <p>Currently supported for GraphQL requests over HTTP and WebSocket in
 * Spring MVC applications.
 *
 * @author Rossen Stoyanchev
 * @see org.springframework.graphql.server.WebGraphQlHandler.Builder#threadLocalAccessor(ThreadLocalAccessor...)
 * @since 1.0.0
 */
public interface ThreadLocalAccessor {

    /**
     * Extract ThreadLocal values and add them to the given Map, so they can be
     * saved and subsequently {@link #restoreValues(Map) restored} around the
     * invocation of data fetchers and exception resolvers.
     *
     * @param container to add extracted ThreadLocal values to
     */
    void extractValues(Map<String, Object> container);

    /**
     * Restore ThreadLocal context by looking up previously
     * {@link #extractValues(Map) extracted} values.
     *
     * @param values previously extracted saved ThreadLocal values
     */
    void restoreValues(Map<String, Object> values);

    /**
     * Reset ThreadLocal context for the given, previously
     * {@link #extractValues(Map) extracted} and then
     * {@link #restoreValues(Map) restored} values.
     *
     * @param values previously extracted saved ThreadLocal values
     */
    void resetValues(Map<String, Object> values);

    /**
     * Create a composite accessor that applies all of the given ThreadLocal accessors.
     *
     * @param accessors the accessors to apply
     * @return the composite accessor
     */
    static ThreadLocalAccessor composite(List<ThreadLocalAccessor> accessors) {
        return new CompositeThreadLocalAccessor(accessors);
    }

}
