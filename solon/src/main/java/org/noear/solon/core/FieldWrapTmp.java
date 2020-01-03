package org.noear.solon.core;

import java.lang.annotation.Annotation;

public class FieldWrapTmp {
    public final FieldWrap fw;
    public final Object obj;

    public FieldWrapTmp(FieldWrap fw, Object obj) {
        this.fw = fw;
        this.obj = obj;
    }

    public Class<?> getType(){
        return fw.type;
    }

    public Annotation[] getAnnoS(){
        return fw.annoS;
    }

    /**
     * 设置字段值
     */
    public void setValue(Object val) {
        try {
            fw.field.setAccessible(true);
            fw.field.set(obj, val);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
