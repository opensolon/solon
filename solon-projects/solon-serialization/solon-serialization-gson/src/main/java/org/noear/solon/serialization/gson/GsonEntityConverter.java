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
package org.noear.solon.serialization.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.LazyReference;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.serialization.AbstractStringEntityConverter;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.gson.impl.DateReadAdapter;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Gson 实体转换器
 *
 * @author noear
 * @since 3.6
 */
public class GsonEntityConverter extends AbstractStringEntityConverter<GsonStringSerializer> {
    public GsonEntityConverter(GsonStringSerializer serializer) {
        super(serializer);

        serializer.getDeserializeConfig().getBuilder().registerTypeAdapter(Date.class, new DateReadAdapter());
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
     * @param p       参数包装器
     * @param pi      参数序位
     * @param pt      参数类型
     * @param bodyRef 主体对象
     */
    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, LazyReference bodyRef) throws Throwable {
        if (p.spec().isRequiredPath() || p.spec().isRequiredCookie() || p.spec().isRequiredHeader()) {
            //如果是 path、cookie, header
            return super.changeValue(ctx, p, pi, pt, bodyRef);
        }

        if (p.spec().isRequiredBody() == false && ctx.paramMap().containsKey(p.spec().getName())) {
            //有可能是path、queryString变量
            return super.changeValue(ctx, p, pi, pt, bodyRef);
        }

        Object bodyObj = bodyRef.get();

        if (bodyObj == null) {
            return super.changeValue(ctx, p, pi, pt, bodyRef);
        }

        if (bodyObj instanceof JsonObject) {
            JsonObject tmp = (JsonObject) bodyObj;

            if (p.spec().isRequiredBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.has(p.spec().getName())) {
                    //支持泛型的转换
                    if (p.spec().isGenericType()) {
                        return serializer.deserialize(tmp.get(p.spec().getName()), p.getGenericType());
                    } else {
                        return serializer.deserialize(tmp.get(p.spec().getName()), pt);
                    }
                }
            }

            //尝试 body 转换
            if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
                return super.changeValue(ctx, p, pi, pt, bodyRef);
            } else {
                if (List.class.isAssignableFrom(pt)) {
                    return null;
                }

                if (pt.isArray()) {
                    return null;
                }

                //支持泛型的转换 如：Map<T>
                if (p.spec().isGenericType()) {
                    return serializer.deserialize(tmp, p.getGenericType());
                } else {
                    return serializer.deserialize(tmp, pt);
                }
            }
        }

        if (bodyObj instanceof JsonArray) {
            JsonArray tmp = (JsonArray) bodyObj;
            //如果参数是非集合类型
            if (!Collection.class.isAssignableFrom(pt)) {
                return null;
            }
            //集合类型转换
            if (p.spec().isGenericType()) {
                //转换带泛型的集合
                return serializer.deserialize(tmp, p.getGenericType());
            } else {
                //不仅可以转换为List 还可以转换成Set
                return serializer.deserialize(tmp, pt);
            }
        }

        return bodyObj;
    }
}