package org.noear.nami.coder.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.Constants;

import java.nio.charset.StandardCharsets;


/**
 * @author noear
 * @since 1.9
 */
public class Fastjson2Encoder implements Encoder {
    public static final Fastjson2Encoder instance = new Fastjson2Encoder();


    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_JSON;
    }

    @Override
    public byte[] encode(Object obj) {
        return JSON.toJSONString(obj,
                        JSONWriter.Feature.BrowserCompatible,
                        JSONWriter.Feature.ReferenceDetection)
                .getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
