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
package org.noear.solon.docs.openapi3.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.Routing;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author noear
 * @since 2.4
 */
public class ActionHolder {
    private final Routing<Handler> routing;
    private final Action action;

    public Routing<Handler> routing(){
        return routing;
    }

    public Action action(){
        return action;
    }


    public Class<?> controllerClz(){
        return action.controller().clz();
    }

    public Set<String> getTags(Operation apiOperationAnno) {
        Tag apiAnno = controllerClz().getAnnotation(Tag.class);

        Set<String> actionTags = new HashSet<>();

        actionTags.add(apiAnno.name());
        actionTags.addAll(Arrays.asList(apiOperationAnno.tags()));
        actionTags.remove("");

        return actionTags;
    }

    public boolean isGet() {
        return routing().method() == MethodType.GET
                || (action.method().getParamWraps().length == 0 && routing().method() == MethodType.ALL);
    }


    public boolean isAnnotationPresent(Class<? extends Annotation> annoClz){
        return action.method().isAnnotationPresent(annoClz);
    }

    public <T extends Annotation> T  getAnnotation(Class<T> annoClz){
        return action.method().getAnnotation(annoClz);
    }


    public <T extends Annotation> T[]  getAnnotationsByType(Class<T> annoClz){
        return action.method().getMethod().getAnnotationsByType(annoClz);
    }

    public ActionHolder(Routing<Handler> routing, Action action){
        this.routing = routing;
        this.action =action;
    }

    @Override
    public String toString() {
        return action.fullName();
    }
}
