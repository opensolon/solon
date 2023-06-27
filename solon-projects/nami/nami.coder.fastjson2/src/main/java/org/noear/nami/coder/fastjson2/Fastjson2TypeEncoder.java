package org.noear.nami.coder.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.noear.nami.Context;
import org.noear.nami.EncoderTyped;
import org.noear.nami.common.ContentTypes;

import java.nio.charset.StandardCharsets;


/**
 * @author noear
 * @since 1.9
 */
public class Fastjson2TypeEncoder implements EncoderTyped {
    public static final Fastjson2TypeEncoder instance = new Fastjson2TypeEncoder();

    @Override
    public String enctype() {
        return ContentTypes.JSON_TYPE_VALUE;
    }

    @Override
    public byte[] encode(Object obj) {
        return JSON.toJSONString(obj,
                        JSONWriter.Feature.BrowserCompatible,
                        JSONWriter.Feature.WriteClassName,
                        JSONWriter.Feature.ReferenceDetection)
                .getBytes(StandardCharsets.UTF_8);

    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
