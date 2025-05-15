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

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.data.annotation.Ds;
import org.noear.solon.lang.Preview;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Ds 构建器
 *
 * @author noear
 * @since 3.3
 */
@Preview("3.3")
public class DsBuilder<T extends Annotation> implements BeanBuilder<T> {
    private static final DsBuilder<Ds> _default = new DsBuilder<>(Ds::value);

    public static DsBuilder<Ds> getDefault() {
        return _default;
    }

    protected final List<DsBuildHandler> handlers = new ArrayList<>();
    protected final Function<T, String> nameMapper;

    public DsBuilder(Function<T, String> nameMapper) {
        this.nameMapper = nameMapper;
    }

    /**
     * 添加类型注入处理器
     */
    public void addHandler(DsBuildHandler handler) {
        handlers.add(handler);
    }


    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, T anno) throws Throwable {
        DsUtils.observeDs(bw.context(), nameMapper.apply(anno), dsWrap -> {
            doBuildHandle(clz, dsWrap);
        });
    }

    /**
     * 注入处理
     */
    protected boolean doBuildHandle(Class<?> clz, BeanWrap dsWrap) {
        //注册处理优先
        for (DsBuildHandler handler : handlers) {
            if (handler.doHandle(clz, dsWrap)) {
                return true;
            }
        }

        return false;
    }
}
