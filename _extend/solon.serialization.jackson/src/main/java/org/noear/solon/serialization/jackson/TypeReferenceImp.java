package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.Type;

/**
 * @author noear 2021/1/17 created
 */
public class TypeReferenceImp<T> extends TypeReference<T> {
    protected final Type _type2;

    public TypeReferenceImp(ParamWrap p) {
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
