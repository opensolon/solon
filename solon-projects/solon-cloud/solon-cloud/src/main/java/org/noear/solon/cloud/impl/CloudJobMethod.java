/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.MethodHandler;

import java.lang.reflect.Method;

/**
 * CloubJob 方法运行器（支持非单例）
 *
 * @author noear
 * @since 2.2
 */
public class CloudJobMethod extends MethodHandler implements CloudJobHandler {
    /**
     * @param beanWrap Bean包装器
     * @param method   函数（外部要控制访问权限）
     */
    public CloudJobMethod(BeanWrap beanWrap, Method method) {
        super(beanWrap, method, true);
    }
}
