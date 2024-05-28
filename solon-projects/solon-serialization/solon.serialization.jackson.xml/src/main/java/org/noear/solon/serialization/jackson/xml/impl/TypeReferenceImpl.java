package org.noear.solon.serialization.jackson.xml.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.Type;

/**
 * @author painter
 * @since 1.2
 * @since 2.8
 */
public class TypeReferenceImpl<T> extends TypeReference<T> {
    protected final Type _type2;

    public TypeReferenceImpl(ParamWrap p) {
        if (p.getGenericType() == null) {
            this._type2 = p.getType();
        } else {
            this._type2 = p.getGenericType();
        }
    }

    @Override
    public Type getType() {
        return _type2;
    }
}
