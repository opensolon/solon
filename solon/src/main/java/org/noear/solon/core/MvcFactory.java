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

import org.noear.solon.core.handle.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Mvc 工厂
 *
 * @author noear
 * @since 2.7
 */
public interface MvcFactory {
    /**
     * 创建动作加载器
     */
    ActionLoader createLoader(BeanWrap wrap);

    /**
     * 创建动作加载器
     */
    ActionLoader createLoader(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping);

    /**
     * 查找动作方式类型
     */
    Set<MethodType> findMethodTypes(Set<MethodType> list, Predicate<Class> checker);

    /**
     * 分析动作参数
     */
    void resolveActionParam(ActionParam vo, AnnotatedElement element);

    /**
     * 确认动作路径
     */
    String postActionPath(BeanWrap bw, String bPath, Method method, String mPath);

    /**
     * 获取动作默认执行器
     */
    ActionExecuteHandler getExecuteHandlerDefault();
}
