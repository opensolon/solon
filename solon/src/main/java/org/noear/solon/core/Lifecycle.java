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
package org.noear.solon.core;

/**
 * 生命周期
 *
 * @author noear
 * @since 1.5
 */
@FunctionalInterface
public interface Lifecycle {
    /**
     * 开始
     */
    void start() throws Throwable;

    /**
     * 开始后
     *
     * @since 2.9
     */
    default void postStart() throws Throwable {
    }

    /**
     * 预停止
     *
     * @since 2.9
     */
    default void preStop() throws Throwable {

    }

    /**
     * 停止
     */
    default void stop() throws Throwable {
    }
}
