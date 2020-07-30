package org.noear.solonclient.serializer;

import org.noear.solonclient.Enctype;
import org.noear.solonclient.ISerializer;

public class FormSerializer implements ISerializer {
    public static final FormSerializer instance = new FormSerializer();

    @Override
    public Enctype enctype() {
        return Enctype.form_urlencoded;
    }

    @Override
    public Object serialize(Object obj) {
        return null;
    }
}
