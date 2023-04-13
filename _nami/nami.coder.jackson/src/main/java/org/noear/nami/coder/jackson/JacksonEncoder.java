package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.ContentTypes;

/**
 * @author noear
 * @since 1.2
 */
public class JacksonEncoder implements Encoder {
    public static final JacksonEncoder instance = new JacksonEncoder();

    ObjectMapper mapper = new ObjectMapper();

    public JacksonEncoder() {
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        // 允许使用未带引号的字段名
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许使用单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    @Override
    public String enctype() {
        return ContentTypes.JSON_VALUE;
    }

    @Override
    public byte[] encode(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
