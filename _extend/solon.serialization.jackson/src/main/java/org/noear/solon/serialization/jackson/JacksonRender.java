package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

public class JacksonRender implements Render {
    ObjectMapper mapper = new ObjectMapper();
    ObjectMapper mapper_serialize = new ObjectMapper();

    private boolean _typedJson;

    public JacksonRender(boolean typedJson) {
        _typedJson = typedJson;

        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper_serialize.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        //没有@type，不好反序列化异常类
//        mapper_serialize.activateDefaultTypingAsProperty(
//                mapper_serialize.getPolymorphicTypeValidator(),
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                "@type");

    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        String txt = null;

        if (_typedJson) {
            //序列化处理
            //
            txt = mapper_serialize.writeValueAsString(obj);
        } else if (ctx.accept().indexOf("/json") > 0) {
            txt = mapper.writeValueAsString(obj);
        } else {
            //非序列化处理
            //
            if (obj == null) {
                return;
            }

            if (obj instanceof Throwable) {
                throw (Throwable) obj;
            }

            if (obj instanceof String && ctx.accept().indexOf("/json") < 0) {
                ctx.output((String) obj); //不能做为json输出
                return;
            }

            txt = mapper.writeValueAsString(obj);
        }

        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "JacksonRender");
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }
}
