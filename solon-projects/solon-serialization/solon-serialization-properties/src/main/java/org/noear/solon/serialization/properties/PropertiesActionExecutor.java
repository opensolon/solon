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
package org.noear.solon.serialization.properties;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.util.LazyReference;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Collection;
import java.util.List;

/**
 * Properties 动作执行器
 *
 * @author noear
 * @since 2.7
 * */
public class PropertiesActionExecutor extends ActionExecuteHandlerDefault {
    private final PropertiesStringSerializer serializer;

    public PropertiesActionExecutor(PropertiesStringSerializer serializer) {
        this.serializer = serializer;
        serializer.getConfig().add(Feature.DisableClassNameRead);
    }

    /**
     * 反序列化配置
     */
    public Options config() {
        return serializer.getConfig();
    }

    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        if (serializer.allowGet() && MethodType.GET.name.equals(ctx.method()) ||
                (serializer.allowPostForm() && (ctx.isFormUrlencoded() || ctx.isMultipartFormData()))) {
            for (String key : ctx.paramMap().keySet()) {
                if (key.indexOf('.') > 0 || key.indexOf('[') > 0) {
                    return true;
                }
            }
        }

        return false;
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
     * @since 1.11 增加 requireBody 支持
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

        ONode tmp = (ONode) bodyObj;

        if (tmp.isObject()) {
            if (p.spec().isRequiredBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.contains(p.spec().getName())) {
                    //支持泛型的转换
                    if (p.spec().isGenericType()) {
                        return tmp.get(p.spec().getName()).toObject(p.getGenericType());
                    } else {
                        return tmp.get(p.spec().getName()).toObject(pt);
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
                    return tmp.toObject(p.getGenericType());
                } else {
                    return tmp.toObject(pt);
                }
            }
        }

        if (tmp.isArray()) {
            //如果参数是非集合类型
            if (!Collection.class.isAssignableFrom(pt)) {
                return null;
            }

            //集合类型转换
            if (p.spec().isGenericType()) {
                //转换带泛型的集合
                return tmp.toObject(p.getGenericType());
            } else {
                //不仅可以转换为List 还可以转换成Set
                return tmp.toObject(pt);
            }
        }

        return tmp.val().getRaw();
    }
}