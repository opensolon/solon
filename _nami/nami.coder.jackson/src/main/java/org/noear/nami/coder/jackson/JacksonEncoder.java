package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.noear.nami.Encoder;
import org.noear.nami.common.Constants;

/**
 * @author noear
 * @since 1.2
 */
public class JacksonEncoder implements Encoder {
    public static final JacksonEncoder instance = new JacksonEncoder();


    ObjectMapper mapper = new ObjectMapper();

    public JacksonEncoder() {
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTypingAsProperty(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "@type");
    }

    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_JSON;
    }

    @Override
    public byte[] encode(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
