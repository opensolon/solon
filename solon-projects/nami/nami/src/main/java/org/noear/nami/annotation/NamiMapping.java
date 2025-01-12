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

import java.lang.annotation.*;

/**
 * 请求映射
 *
 * @author noear
 * @since 1.1
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NamiMapping {
    /**
     * mapping:
     *
     * 例1：GET
     * 例2: GET /xxx/xxx
     * 例3: GET /xxx/{xxx}
     * */
    String value() default "";
    /**
     * 添加头信息
     *
     * 例：{"xxx=xxx","yyy=yyy"}
     * */
    String[] headers() default {};
}
