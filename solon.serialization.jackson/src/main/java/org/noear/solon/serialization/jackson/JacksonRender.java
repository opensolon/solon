package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

import java.io.IOException;
import java.util.Date;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;

public class JacksonRender implements XRender {
    ObjectMapper mapper = new ObjectMapper();
    ObjectMapper mapper_serialize = new ObjectMapper();

    public JacksonRender(){
        mapper_serialize.enableDefaultTypingAsProperty(NON_FINAL, "@type");


//        JsonSerializer<Date> dateSerializer = new JsonSerializer<Date>() {
//            @Override
//            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//                jsonGenerator.writeRaw("new Date(" + date.getTime() + ")");
//            }
//        };
//
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Date.class, dateSerializer);
//
//        mapper_serialize.registerModule(simpleModule);
    }

    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        boolean is_serialize = "serialize".equals(ctx.attr("solon.reader.mode", null));

        String txt = null;

        if (is_serialize) {
            //序列化处理
            //
            txt = mapper_serialize.writeValueAsString(obj);
        } else {
            //非序列化处理
            //
            if (obj == null) {
                return;
            }

            if (obj instanceof Throwable) {
                throw (Throwable) obj;
            }

            if (obj instanceof String) {
                ctx.output((String) obj); //不能做为json输出
                return;
            }

            txt = mapper.writeValueAsString(obj);
        }

        if(XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "JacksonRender");
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }
}
