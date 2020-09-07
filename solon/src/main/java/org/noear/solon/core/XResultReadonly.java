package org.noear.solon.core;

public class XResultReadonly<T> extends XResult<T> {
    public XResultReadonly(T data) {
        super(data);
    }

    public XResultReadonly(int code, String description) {
        super(code, description);
    }

    @Override
    public void setCode(int code) {
        throw new RuntimeException("Thes result is readonly!");
    }

    @Override
    public void setData(T data) {
        throw new RuntimeException("Thes result is readonly!");
    }

    @Override
    public void setDescription(String description) {
        throw new RuntimeException("Thes result is readonly!");
    }
}
