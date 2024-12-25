package org.noear.solon.data.sqlink.api.crud.read;

import org.noear.solon.data.sqlink.core.exception.SqLinkException;

public interface IDynamicTable {
    default <T> T column(String name) {
        throw new SqLinkException("IDynamicTable的函数不能在表达式树以外的地方被调用");
    }
}
