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
package org.noear.solon.docs.models;


import org.noear.solon.Utils;
import org.noear.solon.core.handle.Action;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * 接口资源信息
 *
 * @author noear
 * @since 2.2
 * */
public class ApiResource implements Predicate<Action> , Serializable {

    private transient Predicate<Action> condition;
    private String basePackage;

    public ApiResource() {
        //用于反序列化
    }

    public ApiResource(String basePackage) {
        this.basePackage = basePackage;
    }

    public ApiResource(Predicate<Action> condition) {
        this.condition = condition;
    }

    public String basePackage() {
        return basePackage;
    }

    public ApiResource basePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    @Override
    public boolean test(Action action) {
        boolean isOk = true;
        if (Utils.isNotEmpty(basePackage)) {
            isOk &= action.controller().clz().getName().startsWith(basePackage);
        }

        if (condition != null) {
            isOk &= condition.test(action);
        }

        return isOk;
    }
}
