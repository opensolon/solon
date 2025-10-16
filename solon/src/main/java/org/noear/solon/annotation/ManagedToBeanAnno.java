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
package org.noear.solon.annotation;

import java.lang.annotation.Annotation;

/**
 * Bean 注解包装器（用于中转过渡）
 *
 * @author noear
 * @since 3.5
 */
public class ManagedToBeanAnno implements Bean {
    private Managed anno;

    public ManagedToBeanAnno(Managed anno) {
        this.anno = anno;
    }

    @Override
    public String value() {
        return anno.value();
    }

    @Override
    public String name() {
        return anno.name();
    }

    @Override
    public String tag() {
        return anno.tag();
    }

    @Override
    public boolean typed() {
        return anno.typed();
    }

    @Override
    public int index() {
        return anno.index();
    }

    @Override
    public boolean delivered() {
        return anno.delivered();
    }

    @Override
    public String initMethod() {
        return anno.initMethod();
    }

    @Override
    public String destroyMethod() {
        return anno.destroyMethod();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Managed.class;
    }

    /// ////

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean injected() {
        return anno.autoInject();
    }

    @Override
    public boolean autoInject() {
        return anno.autoInject();
    }

    @Override
    public boolean autoProxy() {
        return anno.autoProxy();
    }
}