package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.Type;

/**
 * @author noear 2021/1/17 created
 */
public class TypeReferenceImp extends TypeReference {
    protected final Type _type2;

    public TypeReferenceImp(Type p) {
        this._type2 = p;
    }

    @Override
    public Type getType() {
        return _type2;
    }
}
