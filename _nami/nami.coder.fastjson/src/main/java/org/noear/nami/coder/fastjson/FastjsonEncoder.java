package org.noear.nami.coder.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.nami.Encoder;
import org.noear.nami.Context;
import org.noear.nami.common.Constants;

import java.nio.charset.StandardCharsets;


public class FastjsonEncoder implements Encoder {

    public static final FastjsonEncoder instance = new FastjsonEncoder();


    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_JSON;
    }

    @Override
    public byte[] encode(Object obj) {
        return JSON.toJSONString(obj,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.DisableCircularReferenceDetect)
                .getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
