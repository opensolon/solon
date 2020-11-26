package org.noear.fairy.encoder;

import org.noear.fairy.Enctype;
import org.noear.fairy.Encoder;
import org.noear.snack.ONode;

public class SnackTypeEncoder implements Encoder {
    public static final SnackTypeEncoder instance = new SnackTypeEncoder();

    @Override
    public Enctype enctype() {
        return Enctype.application_json;
    }

    @Override
    public Object encode(Object obj) {
        return ONode.serialize(obj);
    }
}
