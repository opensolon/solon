package org.noear.solon.data.sqlink.base.metaData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ConverterCache
{
    private ConverterCache()
    {
    }

    private static final Map<Class<? extends IConverter<?, ?>>, IConverter<?, ?>> converterCache = new ConcurrentHashMap<>();

    public static IConverter<?, ?> get(Class<? extends IConverter<?, ?>> c)
    {
        IConverter<?, ?> converter = converterCache.get(c);
        if (converter == null)
        {
            try
            {
                converter = c.newInstance();
                converterCache.put(c, converter);
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }

        }
        return converter;
    }
}
