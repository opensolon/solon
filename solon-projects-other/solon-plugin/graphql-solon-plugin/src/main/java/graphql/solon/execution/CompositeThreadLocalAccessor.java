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

package graphql.solon.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * copy from org.springframework.graphql.execution.CompositeThreadLocalAccessor
 *
 * Default implementation of a composite accessor that is returned from
 * {@link ThreadLocalAccessor#composite(List)}.
 *
 * @author Rossen Stoyanchev
 * @since 1.0.0
 */
class CompositeThreadLocalAccessor implements ThreadLocalAccessor {

    private final List<ThreadLocalAccessor> accessors;

    CompositeThreadLocalAccessor(List<ThreadLocalAccessor> accessors) {
        this.accessors = new ArrayList<>(accessors);
    }

    @Override
    public void extractValues(Map<String, Object> container) {
        this.accessors.forEach((accessor) -> accessor.extractValues(container));
    }

    @Override
    public void restoreValues(Map<String, Object> values) {
        this.accessors.forEach((accessor) -> accessor.restoreValues(values));
    }

    @Override
    public void resetValues(Map<String, Object> values) {
        this.accessors.forEach((accessor) -> accessor.resetValues(values));
    }

}
