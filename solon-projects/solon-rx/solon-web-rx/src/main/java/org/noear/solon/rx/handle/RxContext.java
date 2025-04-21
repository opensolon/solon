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
package org.noear.solon.rx.handle;

import org.noear.solon.core.handle.Context;

/**
 * 响应式上下文
 *
 * @author noear
 * @since 3.1
 */
public interface RxContext {
    /**
     * 属性获取
     */
    <T> T attr(String key);

    /**
     * 属性设置
     */
    void attrSet(String key, Object value);

    ////////////////////////////////////////////////////

    /**
     * 客户端真实IP
     */
    String realIp();

    /**
     * 是否安全
     */
    boolean isSecure();

    ////////////////////////////////////////////////////

    /**
     * 转为经典上下文接口
     */
    Context toContext();
}