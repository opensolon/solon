package org.noear.nami.encoder;


import org.noear.nami.Enctype;
import org.noear.nami.Encoder;

public class FormEncoder implements Encoder {
    public static final FormEncoder instance = new FormEncoder();

    @Override
    public Enctype enctype() {
        return Enctype.form_urlencoded;
    }

    @Override
    public Object encode(Object obj) {
        return null;
    }
}
