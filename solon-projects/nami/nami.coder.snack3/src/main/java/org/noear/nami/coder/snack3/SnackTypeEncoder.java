package org.noear.nami.coder.snack3;

import org.noear.nami.Context;
import org.noear.nami.EncoderTyped;
import org.noear.nami.common.ContentTypes;
import org.noear.snack.ONode;

import java.nio.charset.StandardCharsets;

public class SnackTypeEncoder implements EncoderTyped {
    public static final SnackTypeEncoder instance = new SnackTypeEncoder();

    @Override
    public String enctype() {
        return ContentTypes.JSON_TYPE_VALUE;
    }

    @Override
    public byte[] encode(Object obj) {
        return ONode.serialize(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void pretreatment(Context ctx) {

    }
}
