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

import java.lang.annotation.*;

/**
 * @author noear
 * @since 2.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Condition {
    /**
     * 有类
     */
    Class<?> onClass() default Void.class;

    /**
     * 有类名
     */
    String onClassName() default "";

    /**
     * 有属性（支持多条件）
     * <pre>{@code
     * @Condition(onProperty="${prop1}")
     * @Condition(onProperty="${prop1} == 1")
     * @Condition(onProperty="${prop1} == 1 && ${prop2} == 2")
     * }</pre>
     *
     * @deprecated 3.6 {@link #onExpression()}
     */
    @Deprecated
    String onProperty() default "";

    /**
     * 有表达式（SnEL 表达式，使用更规范）
     *
     * <pre>{@code
     * @Condition(onExpression="${prop1}")
     * @Condition(onExpression="${prop1} == '1'")
     * @Condition(onExpression="${prop1} == '1' && ${prop2} == '2'")
     * }</pre>
     */
    String onExpression() default "";

    /**
     * 缺少 bean type
     */
    Class<?> onMissingBean() default Void.class;

    /**
     * 缺少 bean name
     */
    String onMissingBeanName() default "";

    /**
     * 存在 bean type
     */
    Class<?> onBean() default Void.class;

    /**
     * 存在 bean name
     */
    String onBeanName() default "";

    /**
     * 优先级（满足 onMissing 条件后的运行优先级；越大越优）
     *
     * @since 3.5
     */
    int priority() default 0;
}
