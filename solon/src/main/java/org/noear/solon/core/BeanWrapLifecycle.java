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

import org.noear.solon.Utils;
import org.noear.solon.annotation.Destroy;
import org.noear.solon.annotation.Init;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.IndexUtil;
import org.noear.solon.core.wrap.ClassWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Bean 包装的生命周期
 *
 * @author noear
 * @since 2.8
 */
class BeanWrapLifecycle implements LifecycleBean {
    static final Logger log = LoggerFactory.getLogger(BeanWrapLifecycle.class);

    private BeanWrap bw;
    private Method initMethod;
    private String initMethodName;
    private int initIndex;
    private Method destroyMethod;
    private String destroyMethodName;

    public BeanWrapLifecycle(BeanWrap bw, String initMethodName, String destroyMethodName) {
        this.bw = bw;
        this.initMethodName = initMethodName;
        this.destroyMethodName = destroyMethodName;
    }

    /**
     * 初始化方法
     */
    public Method initMethod() {
        return initMethod;
    }

    /**
     * 注解方法
     */
    public Method destroyMethod() {
        return destroyMethod;
    }

    /**
     * 有效性检测
     */
    public boolean check() {
        if (bw.raw() == null) {
            return false;
        }

        ClassWrap clzWrap = ClassWrap.get(bw.rawClz());

        //按名字找（优先）
        try {
            if (Utils.isNotEmpty(initMethodName)) {
                initMethod = clzWrap.findPublicMethod(initMethodName);
            }

            if (Utils.isNotEmpty(destroyMethodName)) {
                destroyMethod = clzWrap.findPublicMethod(destroyMethodName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        //如果都没有，则安注解找
        if (initMethod == null && destroyMethod == null) {
            //如果不是 jdk 的类（否则没必要）
            if (bw.rawClz().getName().startsWith("java.") == false) {
                //找查注解函数
                for (Method m : clzWrap.findPublicMethods()) {
                    Init initAnno = m.getAnnotation(Init.class);
                    if (initAnno != null) {
                        if (m.getParameters().length == 0) {
                            //只接收没有参数的，支持非公有函数
                            initMethod = m;
                            ClassUtil.accessibleAsTrue(initMethod);
                            initIndex = initAnno.index();
                        }
                    } else {
                        Destroy destroyAnno = m.getAnnotation(Destroy.class);
                        if (destroyAnno != null) {
                            if (m.getParameters().length == 0) {
                                destroyMethod = m;
                                ClassUtil.accessibleAsTrue(destroyMethod);
                            }
                        }
                    }

                    if (initMethod != null && destroyMethod != null) {
                        //如果两个都找到了，就不用找了
                        break;
                    }
                }
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
                log.warn("LifecycleBean not support @Init & @Destroy, class=" + bw.rawClz().getName());
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