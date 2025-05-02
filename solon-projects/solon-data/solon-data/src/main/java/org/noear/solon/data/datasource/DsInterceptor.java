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
package org.noear.solon.data.datasource;

import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.VarHolder;
import org.noear.solon.data.annotation.Ds;
import org.noear.solon.lang.Preview;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Ds 注入器
 *
 * @author noear
 * @since 3.2
 */
@Preview("3.2")
public class DsInterceptor<T extends Annotation> implements BeanInjector<T> {
    private static final DsInterceptor<Ds> _default = new DsInterceptor<>(Ds::value);

    public static DsInterceptor<Ds> getDefault() {
        return _default;
    }

    protected final Map<Class<?>, BiConsumer<VarHolder, BeanWrap>> handlers = new ConcurrentHashMap<>();
    protected final Function<T, String> nameMapper;

    public DsInterceptor(Function<T, String> nameMapper) {
        this.nameMapper = nameMapper;
    }

    /**
     * 添加类型处理器
     */
    public void addHandler(Class<?> type, BiConsumer<VarHolder, BeanWrap> handler) {
        handlers.put(type, handler);
    }

    /**
     * 注入
     */
    @Override
    public void doInject(VarHolder vh, T anno) {
        vh.required(true);

        DsUtils.observeDs(vh.context(), nameMapper.apply(anno), dsWrap -> {
            doInject0(vh, dsWrap);
        });
    }

    /**
     * 注入处理
     */
    protected void doInject0(VarHolder vh, BeanWrap dsWrap) {
        //先按类型处理
        for (Map.Entry<Class<?>, BiConsumer<VarHolder, BeanWrap>> entry : handlers.entrySet()) {
            if (entry.getKey().isAssignableFrom(vh.getType())) {
                entry.getValue().accept(vh, dsWrap);
                return;
            }
        }

        if (DataSource.class.isAssignableFrom(vh.getType())) {
            vh.setValue(dsWrap.get());
            return;
        }

        //如果没有，尝试默认
        BiConsumer<VarHolder, BeanWrap> defHandler = handlers.get(Void.class);

        if (defHandler != null) {
            defHandler.accept(vh, dsWrap);
        }
    }
}