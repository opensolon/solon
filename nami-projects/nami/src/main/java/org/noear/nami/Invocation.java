/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.nami;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Nami - 调用者
 *
 * @author noear
 * @since 1.4
 */
public class Invocation extends Context {
    private List<Filter> filters = new ArrayList<>();
    private int index;

    public Invocation(Config config, Object proxy, Method method, String action, String url, Object body, Filter actuator) {
        super(config, proxy, method, action, url, body);
        this.headers.putAll(config.getHeaders());
        this.filters.addAll(config.getFilters());
        this.filters.add(actuator);
        this.index = 0;
    }

    /**
     * 调用
     * */
    public Result invoke() throws Throwable {
        return filters.get(index++).doFilter(this);
    }
}
