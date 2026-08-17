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
package org.noear.solon.serialization.kryo;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.util.DefaultClassResolver;

import java.io.InvalidClassException;

/**
 * 具有类过滤能力的安全 ClassResolver
 *
 * @author noear
 * @since 4.0.6
 */
public class SafeDefaultClassResolver extends DefaultClassResolver {

    private final KryoClassFilter classFilter;

    public SafeDefaultClassResolver(KryoClassFilter classFilter) {
        this.classFilter = (classFilter != null ? classFilter : KryoClassFilter.defaults());
    }

    public KryoClassFilter getClassFilter() {
        return classFilter;
    }

    @Override
    protected Class<?> getTypeByName(String className) {
        checkAllowed(className);
        return super.getTypeByName(className);
    }

    protected void checkAllowed(String className) {
        if (!classFilter.isAllowed(className)) {
            throw new KryoException(new InvalidClassException(className,
                    "Unauthorized deserialization attempt; Class is not allowed by classFilter "
                            + "(e.g. KryoBytesSerializer#classFilter().allow(\"com.yourapp.\"))"));
        }
    }
}
