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
package org.noear.solon.data.annotation;

import org.noear.solon.data.tran.TranIsolation;
import org.noear.solon.data.tran.TranPolicy;

import java.lang.annotation.*;

/**
 * 事务注解
 *
 * @author noear
 * @since 3.1
 * */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transaction {
    /**
     * 事务传导策略
     * */
    TranPolicy policy() default TranPolicy.required;

    /*
     * 事务隔离等级
     * */
    TranIsolation isolation() default TranIsolation.unspecified;

    /**
     * 只读事务
     * */
    boolean readOnly() default false;

    /**
     * 消息
     * */
    String message() default "";
}
