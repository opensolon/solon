package org.noear.solon.data.sqlink.base.toBean.beancreator;

import java.lang.reflect.InvocationTargetException;

public interface ISetterCaller<T>
{
    void call(T t, Object value) throws InvocationTargetException, IllegalAccessException;
}
