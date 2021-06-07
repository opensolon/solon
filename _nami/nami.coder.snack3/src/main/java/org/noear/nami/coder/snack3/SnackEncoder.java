package org.noear.nami.coder.snack3;

import org.noear.nami.Encoder;
import org.noear.nami.Context;
import org.noear.nami.common.Constants;
import org.noear.snack.ONode;

import java.nio.charset.StandardCharsets;

public class SnackEncoder implements Encoder {
    public static final SnackEncoder instance = new SnackEncoder();

    @Override
    public String enctype() {
        return Constants.CONTENT_TYPE_JSON;
    }

    @Override
    public byte[] encode(Object obj) {
        return ONode.stringify(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
