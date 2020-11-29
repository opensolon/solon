package org.noear.nami.encoder;

import org.noear.nami.Enctype;
import org.noear.nami.Encoder;
import org.noear.snack.ONode;

public class SnackEncoder implements Encoder {
    public static final SnackEncoder instance = new SnackEncoder();

    @Override
    public Enctype enctype() {
        return Enctype.application_json;
    }

    @Override
    public Object encode(Object obj) {
        return ONode.stringify(obj);
    }
}
