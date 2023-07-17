package org.noear.solon.core.wrap;

import java.lang.reflect.Type;

/**
 * 字段 变量申明者
 *
 * @author noear
 * @since 2.4
 */
public class FieldWrapDeclarer extends VarDeclarerBase {
    private final FieldWrap fieldWrap;

    public FieldWrapDeclarer(FieldWrap fieldWrap) {
        super(fieldWrap.field, fieldWrap.field.getName());
        this.fieldWrap = fieldWrap;
    }


    @Override
    public Type getGenericType() {
        return fieldWrap.field.getGenericType();
    }

    @Override
    public Class<?> getType() {
        return fieldWrap.field.getType();
    }
}
