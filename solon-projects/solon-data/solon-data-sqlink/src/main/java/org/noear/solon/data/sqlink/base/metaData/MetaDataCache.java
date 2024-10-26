package org.noear.solon.data.sqlink.base.metaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetaDataCache
{
    private MetaDataCache()
    {
    }

    private static final Map<Class<?>, MetaData> metaDataCache = new ConcurrentHashMap<>();

    public static MetaData getMetaData(Class<?> c)
    {
        if (!metaDataCache.containsKey(c))
        {
            metaDataCache.put(c, new MetaData(c));
        }
        return metaDataCache.get(c);
    }

    public static List<MetaData> getMetaData(List<Class<?>> c)
    {
        List<MetaData> metaDataList=new ArrayList<>(c.size());
        for (Class<?> aClass : c)
        {
            metaDataList.add(getMetaData(aClass));
        }
        return metaDataList;
    }
}
