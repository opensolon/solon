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
package org.noear.solon.data.sqlink.integration.builder;

import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.toBean.beancreator.DefaultBeanCreator;
import org.noear.solon.data.sqlink.base.toBean.beancreator.ISetterCaller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/**
 * aot环境的对象创建器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class AotFastCreator<T> extends DefaultBeanCreator<T> {
    public AotFastCreator(Class<T> target) {
        super(target);
    }

    @Override
    public Supplier<T> initBeanCreator(Class<T> target) {
        if (target.isAnonymousClass()) {
            return unsafeCreator(target);
        }
        else {
            // aot下我们不能使用方法句柄
            if (NativeDetector.inNativeImage()) {
                MetaData metaData = MetaDataCache.getMetaData(target);
                Constructor<T> constructor = (Constructor<T>) metaData.getConstructor();
                return () ->
                {
                    try {
                        return constructor.newInstance();
                    }
                    catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
            }
            else {
                return methodHandleCreator(target);
            }
        }
    }

    @Override
    protected ISetterCaller<T> initBeanSetter(String property) {
        if (target.isAnonymousClass()) {
            return methodBeanSetter(property);
        }
        else {
            // aot下我们不能使用方法句柄
            if (NativeDetector.inNativeImage()) {
                return methodBeanSetter(property);
            }
            else {
                return methodHandleBeanSetter(property);
            }
        }
    }
}
