package org.noear.solon.data.sqlink.plugin.builder;


import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.toBean.beancreator.DefaultBeanCreator;
import org.noear.solon.data.sqlink.base.toBean.beancreator.ISetterCaller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class AotFastCreator<T> extends DefaultBeanCreator<T>
{
    public AotFastCreator(Class<T> target)
    {
        super(target);
    }

    @Override
    public Supplier<T> initBeanCreator(Class<T> target)
    {
        if (target.isAnonymousClass())
        {
            return unsafeCreator(target);
        }
        else
        {
            // aot下我们不能使用方法句柄
            if (NativeDetector.inNativeImage())
            {
                MetaData metaData = MetaDataCache.getMetaData(target);
                Constructor<T> constructor = (Constructor<T>) metaData.getConstructor();
                return () ->
                {
                    try
                    {
                        return constructor.newInstance();
                    }
                    catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
                    {
                        throw new RuntimeException(e);
                    }
                };
            }
            else
            {
                return methodHandleCreator(target);
            }
        }
    }

    @Override
    protected ISetterCaller<T> initBeanSetter(String property)
    {
        if (target.isAnonymousClass())
        {
            return methodBeanSetter(property);
        }
        else
        {
            // aot下我们不能使用方法句柄
            if (NativeDetector.inNativeImage())
            {
                return methodBeanSetter(property);
            }
            else
            {
                return methodHandleBeanSetter(property);
            }
        }
    }
}
