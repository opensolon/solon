package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Collection;
import java.util.List;

/**
 * Json 动作执行器
 *
 * @author noear
 * @author 夜の孤城
 * @since 1.5
 */
public class FastjsonActionExecutor extends ActionExecuteHandlerDefault {
    private static final String label = "/json";

    private final ParserConfig config = new ParserConfig();

    /**
     * 反序列化配置
     * */
    public ParserConfig config(){
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
            return JSON.parse(json, config);
        } else {
            return null;
        }
    }

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

        if (bodyObj instanceof JSONObject) {
            JSONObject tmp = (JSONObject) bodyObj;

            if (p.isRequiredBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.containsKey(p.getName())) {
                    //支持泛型的转换
                    if (p.isGenericType()) {
                        return tmp.getObject(p.getName(), p.getGenericType());
                    } else {
                        return tmp.getObject(p.getName(), pt);
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
                    return tmp.toJavaObject(p.getGenericType());
                } else {
                    return tmp.toJavaObject(pt);
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
            if (p.isGenericType()) {
                //转换带泛型的集合
                return tmp.toJavaObject(p.getGenericType());
            } else {
                //不仅可以转换为List 还可以转换成Set
                return tmp.toJavaObject(pt);
            }
        }

        return bodyObj;
    }
}
