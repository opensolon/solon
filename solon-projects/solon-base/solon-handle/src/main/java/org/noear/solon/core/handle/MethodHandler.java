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
package org.noear.solon.core.handle;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.EgggUtil;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;

/**
 * Method Handler（用于处理 Job 之类的简化函数处理）
 *
 * @author noear
 * @since 2.0
 * */
public class MethodHandler extends AbstractEntityReader implements Handler {
    private final BeanWrap bw;
    private final MethodWrap mw;
    private final boolean allowResult;

    /**
     * @param beanWrap    Bean包装器
     * @param method      函数（外部要控制访问权限）
     * @param allowResult 允许传递结果
     */
    public MethodHandler(BeanWrap beanWrap, Method method, boolean allowResult) {
        this.bw = beanWrap;
        this.mw = new MethodWrap(beanWrap.context(), beanWrap.rawClz(), EgggUtil.getClassEggg(beanWrap.rawClz()).findMethodEgggOrNew(method)).ofHandler();
        this.allowResult = allowResult;
    }


    /**
     * 处理
     */
    @Override
    public void handle(Context c) throws Throwable {
        Object target = bw.get(true);
        Object[] args = doRead(c, target, mw);

        Object rst = mw.invokeByAspect(target, args);

        if (allowResult) {
            c.result = rst;
        }
    }
}