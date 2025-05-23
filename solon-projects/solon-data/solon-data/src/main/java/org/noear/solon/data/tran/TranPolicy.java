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
package org.noear.solon.data.tran;

/**
 * 事务策略
 *
 * @author noear
 * @since 1.0
 * */
public enum TranPolicy {
    /**
     * 默认。支持当前事务，如果没有则创建一个新的（需要入栈）
     */
    required(1),

    /**
     * 新建一个事务，同时将当前事务挂起（需要入栈）
     */
    requires_new(2),

    /**
     * 如果当前有事务，则在当前事务内部嵌套一个事务；否则新建事务（需要入栈）
     * */
    nested(3),

    /**
     * 支持当前事务，如果没有事务则报错
     * */
    mandatory(4),

    /**
     * 支持当前事务，如果没有则不使用事务
     */
    supports(5),

    /**
     * 以无事务的方式执行，如果当前有事务则将其挂起
     */
    not_supported(6),

    /**
     * 以无事务的方式执行，如果当前有事务则报错
     */
    never(7);


    public final int code;

    TranPolicy(int code) {
        this.code = code;
    }
}
