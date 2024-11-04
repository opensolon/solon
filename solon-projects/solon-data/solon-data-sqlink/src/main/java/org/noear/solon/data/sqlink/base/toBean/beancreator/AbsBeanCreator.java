/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.data.sqlink.base.toBean.beancreator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author kiryu1223
 * @since 3.0
 */
public abstract class AbsBeanCreator<T> {
    protected final Class<T> target;
    protected final Supplier<T> supplier;
    protected final Map<String, ISetterCaller<T>> setters = new ConcurrentHashMap<>();

    protected AbsBeanCreator(Class<T> target) {
        this.target = target;
        this.supplier = initBeanCreator(target);
    }

    protected abstract Supplier<T> initBeanCreator(Class<T> target);

    protected abstract ISetterCaller<T> initBeanSetter(String property);

    public Supplier<T> getBeanCreator() {
        return supplier;
    }

    public ISetterCaller<T> getBeanSetter(String property) {
        ISetterCaller<T> setterCaller = setters.get(property);
        if (setterCaller == null) {
            setterCaller = initBeanSetter(property);
            setters.put(property, setterCaller);
        }
        return setterCaller;
    }
}
