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
package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.*;
import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.LazyReference;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Collection;
import java.util.List;

/**
 * Json 动作执行器
 *
 * @author noear
 * @author 夜の孤城
 * @author 暮城留风
 * @since 1.9
 * */
public class Fastjson2ActionExecutor extends ActionExecuteHandlerDefault {
    private final Fastjson2StringSerializer serializer = new Fastjson2StringSerializer();

    public Fastjson2ActionExecutor() {
        serializer.getDeserializeConfig().config();
        serializer.getDeserializeConfig().config(JSONReader.Feature.ErrorOnEnumNotMatch);
    }

    /**
     * 获取序列化接口
     */
    public Fastjson2StringSerializer getSerializer() {
        return serializer;
    }

    /**
     * 反序列化配置
     */
    public JSONReader.Context config() {
        return getSerializer().getDeserializeConfig();
    }

    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        return serializer.matched(ctx, mime);
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
            //如果是 path、cookie, header 变量
            return super.changeValue(ctx, p, pi, pt, bodyRef);
        }

        if (p.spec().isRequiredBody() == false && ctx.paramMap().containsKey(p.spec().getName())) {
            //可能是 path、queryString 变量
            return super.changeValue(ctx, p, pi, pt, bodyRef);
        }

        Object bodyObj = bodyRef.get();

        if (bodyObj == null) {
            return super.changeValue(ctx, p, pi, pt, bodyRef);
        }

        if (bodyObj instanceof JSONObject) {
            JSONObject tmp = (JSONObject) bodyObj;

            if (p.spec().isRequiredBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.containsKey(p.spec().getName())) {
                    //支持泛型的转换
                    if (p.spec().isGenericType()) {
                        return tmp.getObject(p.spec().getName(), p.getGenericType());
                    } else {
                        return tmp.getObject(p.spec().getName(), pt);
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
                    return tmp.to(p.getGenericType());
                } else {
                    return tmp.to(pt);
                }
            }
        }

        if (bodyObj instanceof JSONArray) {
            JSONArray tmp = (JSONArray) bodyObj;
            //如果参数是非集合类型
            if (!Collection.class.isAssignableFrom(pt)) {
                return null;
            }
            //集合类型转换
            if (p.spec().isGenericType()) {
                //转换带泛型的集合
                return tmp.to(p.getGenericType());
            } else {
                //不仅可以转换为List 还可以转换成Set
                return tmp.to(pt);
            }
        }

        return bodyObj;
    }
}