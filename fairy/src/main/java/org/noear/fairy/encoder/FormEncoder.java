package org.noear.fairy.encoder;


import org.noear.fairy.Enctype;
import org.noear.fairy.IEncoder;

public class FormEncoder implements IEncoder {
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
