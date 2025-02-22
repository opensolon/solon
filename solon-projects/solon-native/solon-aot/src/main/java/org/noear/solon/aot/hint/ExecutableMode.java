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
package org.noear.solon.aot.hint;

import java.lang.reflect.Executable;

/**
 * Represent the need of reflection for a given {@link Executable}.
 *
 * @author Stephane Nicoll
 * @since 2.2
 */
public enum ExecutableMode {

    /**
     * Only retrieving the {@link Executable} and its metadata is required.
     * <p>仅仅需要检索某方法或者构造函数</p>
     */
    INTROSPECT,

    /**
     * Full reflection support is required, including the ability to invoke
     * the {@link Executable}.
     * <p>全部反射的能力，包括执行方法</p>
     */
    INVOKE;

    /**
     * Specify if this mode already includes the specified {@code other} mode.
     *
     * @param other the other mode to check
     * @return {@code true} if this mode includes the other mode
     */
    boolean includes(ExecutableMode other) {
        return (other == null || this.ordinal() >= other.ordinal());
    }

}
