package org.noear.nami.coder.json.hutool;

import cn.hutool.json.JSONUtil;
import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.common.Constants;

import java.nio.charset.StandardCharsets;

/**
 * @author noear
 * @since 1.5
 */
public class HutoolJsonTypeEncoder implements Encoder {

    public static final HutoolJsonTypeEncoder instance = new HutoolJsonTypeEncoder();

    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_JSON_TYPE;
    }

    @Override
    public byte[] encode(Object obj) {
        return JSONUtil.toJsonStr(obj)
                .getBytes(StandardCharsets.UTF_8);

    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
