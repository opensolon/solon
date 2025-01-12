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
package org.noear.solon.cloud.metrics.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * 用来记录指标的分布
 *
 * @author noear
 * @since 2.4
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MeterSummary {
    /**
     * 名称
     */
    @Alias("name")
    String value() default "";

    /**
     * 名称
     */
    @Alias("value")
    String name() default "";

    /**
     * 最大期望值
     * */
    double maxValue() default Double.MAX_VALUE;

    /**
     * 最小期望值
     * */
    double minValue() default 1D;

    /**
     * 百分位
     * */
    double[] percentiles() default {};

    /**
     * 百分位柱状图
     * */
    boolean percentilesHistogram() default false;

    double scale() default 1.0D;

    double[] serviceLevelObjectives() default {};

    /**
     * 标签
     */
    String[] tags() default {};

    /**
     * 描述
     * */
    String description() default "";
}
