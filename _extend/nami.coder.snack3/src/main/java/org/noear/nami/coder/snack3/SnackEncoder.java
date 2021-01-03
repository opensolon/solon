package org.noear.nami.coder.snack3;

import org.noear.nami.Enctype;
import org.noear.nami.Encoder;
import org.noear.snack.ONode;

import java.nio.charset.StandardCharsets;

public class SnackEncoder implements Encoder {
    public static final SnackEncoder instance = new SnackEncoder();

    @Override
    public Enctype enctype() {
        return Enctype.application_json;
    }

    @Override
    public byte[] encode(Object obj) {
        return ONode.stringify(obj).getBytes(StandardCharsets.UTF_8);
    }
}
