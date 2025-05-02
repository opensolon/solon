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
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Ds 注入器
 *
 * @author noear
 * @since 3.2
 */
@Preview("3.2")
public class DsInjector<T extends Annotation> implements BeanInjector<T> {
    private static final DsInjector<Ds> _default = new DsInjector<>(Ds::value);

    public static DsInjector<Ds> getDefault() {
        return _default;
    }

    protected final List<BiConsumer<VarHolder, BeanWrap>> handlers = new ArrayList<>();
    protected final Function<T, String> nameMapper;

    public DsInjector(Function<T, String> nameMapper) {
        this.nameMapper = nameMapper;
    }

    /**
     * 添加类型注入处理器
     */
    public void addHandler(BiConsumer<VarHolder, BeanWrap> handler) {
        handlers.add(handler);
    }

    /**
     * 注入
     */
    @Override
    public void doInject(VarHolder vh, T anno) {
        vh.required(true);

        DsUtils.observeDs(vh.context(), nameMapper.apply(anno), dsWrap -> {
            doInjectHandle(vh, dsWrap);
        });
    }

    /**
     * 注入处理
     */
    protected void doInjectHandle(VarHolder vh, BeanWrap dsWrap) {
        //注册处理优先
        for (BiConsumer<VarHolder, BeanWrap> handler : handlers) {
            handler.accept(vh, dsWrap);
            if (vh.isDone()) {
                return;
            }
        }

        //默认处理
        if (DataSource.class.isAssignableFrom(vh.getType())) {
            vh.setValue(dsWrap.get());
            return;
        }
    }
}