package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;

/**
 * @author noear
 * @since 1.2
 */
public class TypeReferenceImp<T> extends TypeReference<T> {
    protected final Type _type2;

    public TypeReferenceImp(Type p) {
        this._type2 = p;
    }

    @Override
    public Type getType() {
        return _type2;
    }
}
