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
package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * 云端事件订阅
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudEvent {
    /**
     * 主题，支持${xxx}配置
     * */
    @Alias("topic")
    String value() default "";

    /**
     * 主题，支持${xxx}配置
     * */
    @Alias("value")
    String topic() default "";

    /**
     * 标签
     * */
    String tag() default "";

    /**
     * 订阅级别
     * */
    EventLevel level() default EventLevel.cluster;

    /**
     * 分组，支持${xxx}配置
     * */
    String group() default "";

    /**
     * 通道：用于同时支持多个消息框架
     * */
    String channel() default "";

    /**
     * 服务质量：只对部分适配有用（0，最多交付一次；1，至少交付一次；2，只交付一次）
     * */
    int qos() default 1;
}
