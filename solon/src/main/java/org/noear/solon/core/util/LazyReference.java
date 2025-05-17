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
package org.noear.solon.core.util;

/**
 * 懒引用
 *
 * @author noear
 * @since 3.3
 */
public class LazyReference<T extends Object> implements SupplierEx<T> {
    private final SupplierEx<T> supplier;
    private T value;

    public LazyReference(SupplierEx<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() throws Throwable {
        if (value == null) {
            value = supplier.get();
        }

        return value;
    }
}