package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.noear.nami.Encoder;
import org.noear.nami.common.Constants;

/**
 * @author noear 2021/1/17 created
 */
public class JacksonTypeEncoder implements Encoder {
    public static final JacksonTypeEncoder instance = new JacksonTypeEncoder();

    ObjectMapper mapper = new ObjectMapper();

    public JacksonTypeEncoder(){
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

//        mapper.activateDefaultTypingAsProperty(
//                mapper.getPolymorphicTypeValidator(),
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                "@type");
    }

    @Override
    public String enctype() {
        return Constants.ct_json;
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
