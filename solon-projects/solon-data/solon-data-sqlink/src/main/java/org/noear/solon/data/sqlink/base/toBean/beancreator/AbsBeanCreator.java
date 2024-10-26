package org.noear.solon.data.sqlink.base.toBean.beancreator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class AbsBeanCreator<T>
{
    protected final Class<T> target;
    protected final Supplier<T> supplier;
    protected final Map<String, ISetterCaller<T>> setters = new ConcurrentHashMap<>();

    protected AbsBeanCreator(Class<T> target)
    {
        this.target = target;
        this.supplier = initBeanCreator(target);
    }

    protected abstract Supplier<T> initBeanCreator(Class<T> target);

    protected abstract ISetterCaller<T> initBeanSetter(String property);

    public Supplier<T> getBeanCreator()
    {
        return supplier;
    }

    public ISetterCaller<T> getBeanSetter(String property)
    {
        ISetterCaller<T> setterCaller = setters.get(property);
        if (setterCaller == null)
        {
            setterCaller = initBeanSetter(property);
            setters.put(property, setterCaller);
        }
        return setterCaller;
    }
}
