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
package org.noear.solon.core.mvc;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.MvcFactory;
import org.noear.solon.core.handle.*;

import java.lang.reflect.AnnotatedElement;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Mvc 对接工厂默认实现
 *
 * @author noear
 * @since 2.7
 */
public class MvcFactoryDefault implements MvcFactory {
    public ActionLoader createLoader(BeanWrap wrap) {
        return new ActionLoaderDefault(wrap);
    }

    public ActionLoader createLoader(BeanWrap wrap, String mapping) {
        return createLoader(wrap, mapping, wrap.remoting(), null, true);
    }

    public ActionLoader createLoader(BeanWrap wrap, String mapping, boolean remoting) {
        return createLoader(wrap, mapping, remoting, null, true);
    }

    public ActionLoader createLoader(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
        return new ActionLoaderDefault(wrap, mapping, remoting, render, allowMapping);
    }

    @Override
    public Set<MethodType> findMethodTypes(Set<MethodType> list, Predicate<Class> checker) {
        return MethodTypeResolver.findAndFill(list, checker);
    }

    @Override
    public void resolveParam(ActionParam vo, AnnotatedElement element) {
        ActionParamResolver.resolve(vo, element);
    }

    private final ActionExecuteHandler executeHandlerDefault = new ActionExecuteHandlerDefault();

    @Override
    public ActionExecuteHandler getExecuteHandlerDefault() {
        return executeHandlerDefault;
    }
}
