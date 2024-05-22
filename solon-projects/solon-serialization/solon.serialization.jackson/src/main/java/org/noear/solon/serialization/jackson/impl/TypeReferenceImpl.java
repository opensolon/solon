package org.noear.solon.serialization.jackson.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.Type;

/**
 * @author noear
 * @since 1.2
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
