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

import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.wrap.ClassWrap;

import java.lang.reflect.Type;
import java.util.Properties;

/**
 * @author noear
 * @since 1.6
 */
public class PropsConverter {
    private static PropsConverter global;

    public static PropsConverter global() {
        return global;
    }

    public static void globalSet(PropsConverter instance) {
        if (instance != null) {
            PropsConverter.global = instance;
        }
    }

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        PropsConverter tmp = ClassUtil.tryInstance("org.noear.solon.extend.impl.PropsConverterExt");

        if (tmp == null) {
            global = new PropsConverter();
        } else {
            global = tmp;
        }
    }


    /**
     * 转换
     *
     * @param props      属性
     * @param target     目标
     * @param targetClz  目标类
     * @param targetType 目标类型
     */
    public <T> T convert(Properties props, T target, Class<T> targetClz, Type targetType) {
        if (target == null) {
            return ClassWrap.get(targetClz).newBy(props);
        } else {
            if (props != null && props.size() > 0) {
                ClassWrap.get(target.getClass()).fill(target, props::getProperty);
            }
            return target;
        }
    }

    public <T> T convert(Properties props, Class<T> targetClz) {
        return convert(props, null, targetClz, null);
    }
}
