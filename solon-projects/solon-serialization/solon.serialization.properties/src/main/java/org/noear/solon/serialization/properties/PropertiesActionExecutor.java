package org.noear.solon.serialization.properties;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.NameValues;
import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Properties 动作执行器
 *
 * @author noear
 * @since 2.7
 * */
public class PropertiesActionExecutor extends ActionExecuteHandlerDefault {
    private final Options config = Options.def().add(Feature.DisableClassNameRead);
    private boolean allowGet = true;
    private boolean allowPostForm = false;

    /**
     * 允许处理 Get 请求
     */
    public void allowGet(boolean allowGet) {
        this.allowGet = allowGet;
    }

    /**
     * 允许处理 PostForm 请求
     */
    public void allowPostForm(boolean allowPostForm) {
        this.allowPostForm = allowPostForm;
    }

    /**
     * @deprecated 2.8
     * */
    @Deprecated
    public void includeFormUrlencoded(boolean allowPostForm){
        this.allowPostForm(allowPostForm);
    }

    /**
     * 反序列化配置
     */
    public Options config() {
        return config;
    }

    @Override
    public boolean matched(Context ctx, String ct) {
        if (allowGet && MethodType.GET.name.equals(ctx.method()) ||
                (allowPostForm && (ctx.isFormUrlencoded() || ctx.isMultipartFormData()))) {
            for (Map.Entry<String, List<String>> kv : ctx.paramsMap().entrySet()) {
                if (kv.getKey().indexOf('.') > 0 || kv.getKey().indexOf('[') > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected Object changeBody(Context ctx, MethodWrap mWrap) throws Exception {
        NameValues nameValues = new NameValues();
        for (Map.Entry<String, List<String>> kv : ctx.paramsMap().entrySet()) {
            for (String val : kv.getValue()) {
                nameValues.add(kv.getKey(), val);
            }
        }

        return ONode.loadObj(nameValues);
    }

    /**
     * @since 1.11 增加 requireBody 支持
     */
    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (p.isRequiredPath() || p.isRequiredCookie() || p.isRequiredHeader()) {
            //如果是 path、cookie, header
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (p.isRequiredBody() == false && ctx.paramMap().containsKey(p.getName())) {
            //有可能是path、queryString变量
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (bodyObj == null) {
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        ONode tmp = (ONode) bodyObj;

        if (tmp.isObject()) {
            if (p.isRequiredBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.contains(p.getName())) {
                    //支持泛型的转换
                    if (p.isGenericType()) {
                        return tmp.get(p.getName()).toObject(p.getGenericType());
                    } else {
                        return tmp.get(p.getName()).toObject(pt);
                    }
                }
            }


            //尝试 body 转换
            if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
                return super.changeValue(ctx, p, pi, pt, bodyObj);
            } else {
                if (List.class.isAssignableFrom(pt)) {
                    return null;
                }

                if (pt.isArray()) {
                    return null;
                }

                //支持泛型的转换 如：Map<T>
                if (p.isGenericType()) {
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
            if (p.isGenericType()) {
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
