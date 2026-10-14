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
package org.noear.nami.annotation;

import org.noear.nami.NamiConfiguration;
import org.noear.nami.NamiConfigurationDefault;

import java.lang.annotation.*;

/**
 * Nami 客户端
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NamiClient {

    /**
     * 完整的url地址（url）
     * */
    String url() default "";

    /**
     * 服务组
     * */
    String group() default "";

    /**
     * 服务名
     * */
    String name() default "";

    /**
     * 路径
     * */
    String path() default "";

    /**
     * 添加头信息
     *
     * 例：{"xxx=xxx","yyy=yyy"}
     * */
    String[] headers() default {};

    /**
     * 负载（用于方便演示，设定固定负载）
     * */
    String[] upstream() default {};

    /**
     * 超时（单为：秒）
     * */
    int timeout() default 0;

    /**
     * 心跳（单为：秒）
     * */
    int heartbeat() default 0;

    /**
     * 本地优化（如果为 true，则先找本地实现组件）
     * */
    boolean localFirst() default false;

    /**
     * 指定配置器
     * */
    Class<? extends NamiConfiguration> configuration() default NamiConfigurationDefault.class;

    /**
     * 容错处理
     * */
    Class<?> fallback() default void.class;

    /**
     * 容错处理工厂
     * */
    Class<?> fallbackFactory() default void.class;
}

