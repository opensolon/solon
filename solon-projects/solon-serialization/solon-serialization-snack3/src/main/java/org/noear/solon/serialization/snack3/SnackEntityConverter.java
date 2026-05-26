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
package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.LazyReference;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.serialization.AbstractStringEntityConverter;
import org.noear.solon.serialization.SerializerNames;

import java.util.Collection;
import java.util.List;

/**
 * Snack 实体转换器
 *
 * @author noear
 * @since 3.6
 */
public class SnackEntityConverter extends AbstractStringEntityConverter<SnackStringSerializer> {
    public SnackEntityConverter(SnackStringSerializer serializer) {
        super(serializer);

        serializer.getDeserializeConfig().addFeatures(Feature.DisableClassNameRead);
    }

    /**
     * 后缀或名字映射
     */
    @Override
    public String[] mappings() {
        return new String[]{SerializerNames.AT_JSON};
    }

    /**
     * 转换 body
     *
     * @param ctx   请求上下文
     * @param mWrap 函数包装器
     */
    @Override
    protected Object changeBody(Context ctx, MethodWrap mWrap) throws Exception {
        return serializer.deserializeFromBody(ctx);
    }

    /**
     * 转换 value
     *
     * @param ctx     请求上下文
     * @param pWrap   参数包装器
     * @param pIdx    参数序位
     * @param pt      参数类型
     * @param bodyRef 主体对象
     * @since 1.11 增加 requireBody 支持
     */
    @Override
    protected Object changeValue(Context ctx, ParamWrap pWrap, int pIdx, Class<?> pt, LazyReference bodyRef) throws Throwable {
        if (pWrap.spec().isRequiredPath() || pWrap.spec().isRequiredCookie() || pWrap.spec().isRequiredHeader()) {
            //如果是 path、cookie, header
            return super.changeValue(ctx, pWrap, pIdx, pt, bodyRef);
        }

        if (pWrap.spec().isRequiredBody() == false && ctx.paramMap().containsKey(pWrap.spec().getName())) {
            //有可能是path、queryString变量
            return super.changeValue(ctx, pWrap, pIdx, pt, bodyRef);
        }

        Object bodyObj = bodyRef.get();

        if (bodyObj == null) {
            return super.changeValue(ctx, pWrap, pIdx, pt, bodyRef);
        }

        ONode tmp = (ONode) bodyObj;

        if (tmp.isObject()) {
            if (pWrap.spec().isRequiredBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.contains(pWrap.spec().getName())) {
                    //支持泛型的转换
                    return tmp.get(pWrap.spec().getName()).toObject(pWrap.getGenericType());
                }
            }


            //尝试 body 转换
            if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
                return super.changeValue(ctx, pWrap, pIdx, pt, bodyRef);
            } else {
                if (List.class.isAssignableFrom(pt)) {
                    return null;
                }

                if (pt.isArray()) {
                    return null;
                }

                //支持泛型的转换 如：Map<T>
                return tmp.toObject(pWrap.getGenericType());
            }
        }

        if (tmp.isArray()) {
            //如果参数是非集合类型
            if (!Collection.class.isAssignableFrom(pt)) {
                return null;
            }

            //集合类型转换
            return tmp.toObject(pWrap.getGenericType());
        }

        return tmp.val().getRaw();
    }
}