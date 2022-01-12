package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

public class JacksonActionExecutor extends ActionExecutorDefault {
    private static final String label = "/json";

    ObjectMapper mapper_type = new ObjectMapper();

    public JacksonActionExecutor() {
        mapper_type.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper_type.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper_type.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper_type.activateDefaultTypingAsProperty(
                mapper_type.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "@type");
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

    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {

        if (ctx.paramMap().containsKey(p.getName())) {
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (bodyObj == null) {
            return null;
        }

        JsonNode tmp = (JsonNode) bodyObj;

        if (tmp.isObject()) {
            if (tmp.has(p.getName())) {
                JsonNode m1 = tmp.get(p.getName());

                return mapper_type.readValue(mapper_type.treeAsTokens(m1), new TypeReferenceImp<>(p));
            } else if (ctx.paramMap().containsKey(p.getName())) {
                //有可能是path变量
                //
                return super.changeValue(ctx, p, pi, pt, bodyObj);
            } else {
                if(List.class.isAssignableFrom(p.getType())){
                    return null;
                }

                if(p.getType().isArray()){
                    return null;
                }

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
