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
package org.noear.solon.data.sqlink.core.page;

import java.util.List;

/**
 * 默认分页结果
 *
 * @author kiryu1223
 * @since 3.0
 */
public class DefaultPagedResult<T> implements PagedResult<T> {
    private final long total;
    private final List<T> data;

    public DefaultPagedResult(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public List<T> getData() {
        return data;
    }
}
