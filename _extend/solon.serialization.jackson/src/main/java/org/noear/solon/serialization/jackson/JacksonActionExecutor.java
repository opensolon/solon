package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.ParameterizedType;

public class JacksonActionExecutor extends ActionExecutorDefault {
    private static final String label = "/json";

    ObjectMapper mapper_type = new ObjectMapper();

    public JacksonActionExecutor(){
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
        return mapper_type.readTree(ctx.bodyNew());
    }

    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return null;
        }


        if (ctx.paramMap().containsKey(p.getName())) {
            return super.changeValue(ctx, p, pi, pt, bodyObj);
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
                //return tmp.toObject(pt);

                return mapper_type.readValue(mapper_type.treeAsTokens(tmp), new TypeReferenceImp<>(p));
            }
        }

        if (tmp.isArray()) {
            //List<T> 类型转换
            ParameterizedType gp = p.getGenericType();
            if (gp != null) {
                return mapper_type.readValues(mapper_type.treeAsTokens(tmp), (Class) gp.getActualTypeArguments()[0]);
            }

            return mapper_type.readValue(mapper_type.treeAsTokens(tmp), new TypeReferenceImp<>(p));
        }

        //return tmp.val().getRaw();
        return mapper_type.readValue(mapper_type.treeAsTokens(tmp), new TypeReferenceImp<>(p));
    }
}
