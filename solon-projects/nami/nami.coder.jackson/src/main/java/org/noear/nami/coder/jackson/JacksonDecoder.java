package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.EncoderTyped;
import org.noear.nami.Result;
import org.noear.nami.common.Constants;
import org.noear.nami.common.ContentTypes;

import java.lang.reflect.Type;

/**
 * @author noear
 * @since 1.2
 */
public class JacksonDecoder implements Decoder {
    public static final JacksonDecoder instance = new JacksonDecoder();


    ObjectMapper mapper_type = new ObjectMapper();

    public JacksonDecoder(){
        mapper_type.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper_type.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper_type.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper_type.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper_type.activateDefaultTypingAsProperty(
                mapper_type.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        mapper_type.registerModule(new JavaTimeModule());
        // 允许使用未带引号的字段名
        mapper_type.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许使用单引号
        mapper_type.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    @Override
    public String enctype() {
        return ContentTypes.JSON_VALUE;
    }

    @Override
    public <T> T decode(Result rst, Type type) {
        String str = rst.bodyAsString();

        Object returnVal = null;
        try {
            if (str == null) {
                return (T) str;
            }

            if (str.contains("\"stackTrace\":[{")) {
                returnVal = mapper_type.readValue(str, RuntimeException.class);
            }else {
                returnVal = mapper_type.readValue(str, new TypeReferenceImp(type));
            }

        } catch (Throwable ex) {
            returnVal = ex;
        }

        if (returnVal != null && returnVal instanceof Throwable) {
            if (returnVal instanceof RuntimeException) {
                throw (RuntimeException) returnVal;
            } else {
                throw new RuntimeException((Throwable) returnVal);
            }
        } else {
            return (T) returnVal;
        }
    }

    @Override
    public void pretreatment(Context ctx) {
        if (ctx.config.getEncoder() instanceof EncoderTyped) {
            ctx.headers.put(Constants.HEADER_SERIALIZATION, Constants.AT_TYPE_JSON);
        }

        ctx.headers.put(Constants.HEADER_ACCEPT, ContentTypes.JSON_VALUE);
    }
}
