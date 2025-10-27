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
package org.noear.solon.extend.impl;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.solon.Utils;
import org.noear.solon.core.PropsConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Properties;

/**
 * 属性转换器-静态扩展实现
 *
 * @author noear
 * @since 1.6
 * @since 3.7
 */
public class PropsConverterExt extends PropsConverter {
    @Override
    public <T> T convert(Properties props, T target, Class<T> targetClz, Type targetType) {
        if (target == null) {
            try {
                //尝试用构造函数注入
                Constructor constructor = targetClz.getConstructor(Properties.class);
                if (constructor != null) {
                    return (T) constructor.newInstance(props);
                }
            } catch (NoSuchMethodException e) {
                //跳过
            } catch (Throwable e) {
                e = Utils.throwableUnwrap(e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }

            if (targetType == null) {
                targetType = targetClz;
            }

            return ONode.ofBean(props, Feature.Write_AllowUseSetter).toBean(targetType);
        } else {
            //bindTo 可能会返回为 null
            if (props != null && props.size() > 0) {
                ONode.ofBean(props, Feature.Write_AllowUseSetter).bindTo(target);
            }

            return target;
        }
    }
}
