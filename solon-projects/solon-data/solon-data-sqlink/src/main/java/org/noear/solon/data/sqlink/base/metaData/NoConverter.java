package org.noear.solon.data.sqlink.base.metaData;

public class NoConverter implements IConverter<Object, Object>
{
    @Override
    public Object toDb(Object value, PropertyMetaData propertyMetaData)
    {
        return value;
    }

    @Override
    public Object toJava(Object value, PropertyMetaData propertyMetaData)
    {
        return value;
    }
}
