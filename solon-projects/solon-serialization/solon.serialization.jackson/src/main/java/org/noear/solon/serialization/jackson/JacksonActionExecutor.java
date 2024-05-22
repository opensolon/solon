package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.Utils;
import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Collection;
import java.util.List;

/**
 * @author noear
 * @since 1.2
 * */
public class JacksonActionExecutor extends ActionExecuteHandlerDefault {
    private static final String label = "/json";

    private JacksonStringSerializer serializer = new JacksonStringSerializer();

    public ObjectMapper config(){
        return serializer.getConfig();
    }

    public void config(ObjectMapper objectMapper){
        serializer.setConfig(objectMapper);
    }

    public JacksonActionExecutor() {
        serializer.getConfig().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        serializer.getConfig().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        serializer.getConfig().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        serializer.getConfig().activateDefaultTypingAsProperty(
                serializer.getConfig().getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        // 注册 JavaTimeModule ，以适配 java.time 下的时间类型
        serializer.getConfig().registerModule(new JavaTimeModule());
        // 允许使用未带引号的字段名
        serializer.getConfig().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许使用单引号
        serializer.getConfig().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
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
            return serializer.deserialize(json, null);
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

        JsonNode tmp = (JsonNode) bodyObj;

        if (tmp.isObject()) {
            if (p.isRequiredBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.has(p.getName())) {
                    JsonNode m1 = tmp.get(p.getName());

                    return serializer.getConfig().readValue(serializer.getConfig().treeAsTokens(m1), new TypeReferenceImp<>(p));
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
                return serializer.getConfig().readValue(serializer.getConfig().treeAsTokens(tmp), new TypeReferenceImp<>(p));
            }
        }

        if (tmp.isArray()) {
            //如果参数是非集合类型
            if (!Collection.class.isAssignableFrom(pt)) {
                return null;
            }

            return serializer.getConfig().readValue(serializer.getConfig().treeAsTokens(tmp), new TypeReferenceImp<>(p));
        }

        //return tmp.val().getRaw();
        if (tmp.isValueNode()) {
            return serializer.getConfig().readValue(serializer.getConfig().treeAsTokens(tmp), new TypeReferenceImp<>(p));
        } else {
            return null;
        }
    }
}
