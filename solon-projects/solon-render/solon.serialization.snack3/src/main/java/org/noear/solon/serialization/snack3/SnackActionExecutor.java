package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Collection;
import java.util.List;

/**
 * Json 动作执行器
 *
 * @author noear
 * @since 1.0
 * */
public class SnackActionExecutor extends ActionExecuteHandlerDefault {
    private static final String label = "/json";

    private final Options config = Options.def().add(Feature.DisableClassNameRead);

    /**
     * 反序列化配置
     * */
    public Options config(){
        return config;
    }

    @Override
    public boolean matched(Context ctx, String ct) {
        if (ct != null && ct.contains(label)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Object changeBody(Context ctx, MethodWrap mWrap) throws Exception {
        String json = ctx.bodyNew();

        if (Utils.isNotEmpty(json)) {
            return ONode.loadStr(json, config);
        } else {
            return null;
        }
    }

    /**
     * @since 1.11 增加 requireBody 支持
     * */
    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if(p.isRequiredPath() || p.isRequiredCookie() || p.isRequiredHeader()){
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
                    }else{
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
                }else{
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
            }else{
                //不仅可以转换为List 还可以转换成Set
                return tmp.toObject(pt);
            }
        }

        return tmp.val().getRaw();
    }
}
