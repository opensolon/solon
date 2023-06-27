package org.noear.nami.coder.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.nami.Context;
import org.noear.nami.EncoderTyped;
import org.noear.nami.common.ContentTypes;

import java.nio.charset.StandardCharsets;


public class FastjsonTypeEncoder implements EncoderTyped {

    public static final FastjsonTypeEncoder instance = new FastjsonTypeEncoder();

    @Override
    public String enctype() {
        return ContentTypes.JSON_TYPE_VALUE;
    }

    @Override
    public byte[] encode(Object obj) {
        return JSON.toJSONString(obj,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect)
                .getBytes(StandardCharsets.UTF_8);

    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
