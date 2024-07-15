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
package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Destroy;
import org.noear.solon.annotation.Init;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.util.IndexUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.wrap.ClassWrap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author noear
 * @since 2.8
 */
class BeanWrapLifecycle implements LifecycleBean {
    private BeanWrap bw;
    private Method initMethod;
    private int initIndex;
    private Method destroyMethod;

    public BeanWrapLifecycle(BeanWrap bw) {
        this.bw = bw;
    }

    public Method initMethod() {
        return initMethod;
    }

    public Method destroyMethod() {
        return destroyMethod;
    }

    public boolean check() {
        if (bw.raw() == null) {
            return false;
        }

        ClassWrap clzWrap = ClassWrap.get(bw.rawClz());

        //找查注解函数
        for (Method m : clzWrap.getDeclaredMethods()) {
            Init initAnno = m.getAnnotation(Init.class);
            if (initAnno != null) {
                if (m.getParameters().length == 0) {
                    //只接收没有参数的，支持非公有函数
                    initMethod = m;
                    initMethod.setAccessible(true);
                    initIndex = initAnno.index();
                }
            } else {
                Destroy destroyAnno = m.getAnnotation(Destroy.class);
                if (destroyAnno != null) {
                    if (m.getParameters().length == 0) {
                        destroyMethod = m;
                        destroyMethod.setAccessible(true);
                    }
                }
            }

            if (initMethod != null && destroyMethod != null) {
                //如果两个都找到了，就不用找了
                break;
            }
        }

        //处理顺序位
        if (initMethod != null) {
            if (initIndex == 0) {
                //如果为0，则自动识别
                initIndex = IndexUtil.buildLifecycleIndex(bw.rawClz());
            }
        }

        boolean isOk = initMethod != null || destroyMethod != null;

        if (isOk) {
            if (bw.raw() instanceof LifecycleBean) {
                LogUtil.global().warn("LifecycleBean not support @Init & @Destroy, class=" + bw.rawClz().getName());
                return false;
            }
        }

        return isOk;
    }

    /**
     * 顺序位
     */
    public int index() {
        return initIndex;
    }

    @Override
    public void start() throws Throwable {
        if (initMethod != null) {
            try {
                initMethod.invoke(bw.raw());
            } catch (InvocationTargetException e) {
                Throwable e2 = e.getTargetException();
                throw Utils.throwableUnwrap(e2);
            }
        }
    }

    @Override
    public void stop() throws Throwable {
        if (destroyMethod != null) {
            try {
                destroyMethod.invoke(bw.raw());
            } catch (InvocationTargetException e) {
                Throwable e2 = e.getTargetException();
                throw Utils.throwableUnwrap(e2);
            }
        }
    }
}
