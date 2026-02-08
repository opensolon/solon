/*
 * Copyright 2017-2026 noear.org and authors
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
package org.noear.solon.shell.annotation;

import java.lang.annotation.*;

/**
 * 标记 Solon Shell 具体命令方法
 *
 * @author noear
 * @author shenmk
 * @since 3.9.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Command {
    /**
     * 命令名（终端输入的指令，必填，全局唯一）
     */
    String value();

    /**
     * 命令描述（用于 help 命令展示，可选）
     */
    String description() default "";
}