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
public class FastjsonTypeEncoder implements Encoder {
    public static final FastjsonTypeEncoder instance = new FastjsonTypeEncoder();

    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_JSON_TYPE;
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
