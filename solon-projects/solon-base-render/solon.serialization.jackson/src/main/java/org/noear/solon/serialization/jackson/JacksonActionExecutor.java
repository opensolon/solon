package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Collection;
import java.util.List;

/**
 * @author noear
 * @since 1.2
 * */
public class JacksonActionExecutor extends ActionExecutorDefault {
    private static final String label = "/json";

    private final ObjectMapper mapper_type = new ObjectMapper();

    public ObjectMapper config(){
        return mapper_type;
    }

    public JacksonActionExecutor() {
        mapper_type.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper_type.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper_type.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper_type.activateDefaultTypingAsProperty(
                mapper_type.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        // 注册 JavaTimeModule ，以适配 java.time 下的时间类型
        mapper_type.registerModule(new JavaTimeModule());
        // 允许使用未带引号的字段名
        mapper_type.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许使用单引号
        mapper_type.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
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
    protected Object changeBody(Context ctx) throws Exception {
        String json = ctx.bodyNew();

        if (Utils.isNotEmpty(json)) {
            return mapper_type.readTree(json);
        } else {
            return null;
        }
    }

    /**
     * @since 1.11 增加 requireBody 支持
     * */
    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (p.requireBody() == false && ctx.paramMap().containsKey(p.getName())) {
            //有可能是path、queryString变量
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (bodyObj == null) {
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        JsonNode tmp = (JsonNode) bodyObj;

        if (tmp.isObject()) {
            if (p.requireBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.has(p.getName())) {
                    JsonNode m1 = tmp.get(p.getName());

                    return mapper_type.readValue(mapper_type.treeAsTokens(m1), new TypeReferenceImp<>(p));
                }
            }

            //尝试 body 转换
            if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
                return super.changeValue(ctx, p, pi, pt, bodyObj);
            } else {
                if (List.class.isAssignableFrom(p.getType())) {
                    return null;
                }

                if (p.getType().isArray()) {
                    return null;
                }

                //支持泛型的转换 如：Map<T>
                return mapper_type.readValue(mapper_type.treeAsTokens(tmp), new TypeReferenceImp<>(p));
            }
        }

        if (tmp.isArray()) {
            //如果参数是非集合类型
            if (!Collection.class.isAssignableFrom(pt)) {
                return null;
            }

            return mapper_type.readValue(mapper_type.treeAsTokens(tmp), new TypeReferenceImp<>(p));
        }

        //return tmp.val().getRaw();
        if (tmp.isValueNode()) {
            return mapper_type.readValue(mapper_type.treeAsTokens(tmp), new TypeReferenceImp<>(p));
        } else {
            return null;
        }
    }
}
