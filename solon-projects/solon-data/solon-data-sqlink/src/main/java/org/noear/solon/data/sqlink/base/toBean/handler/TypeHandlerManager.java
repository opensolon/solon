package org.noear.solon.data.sqlink.base.toBean.handler;

import org.noear.solon.data.sqlink.base.toBean.handler.impl.datetime.*;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.number.*;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.other.URLTypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.varchar.CharTypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.impl.varchar.StringTypeHandler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeHandlerManager
{
    private static final Map<Type, ITypeHandler<?>> cache = new HashMap<>();
    private static final UnKnowTypeHandler<?> unKnowTypeHandler = new UnKnowTypeHandler<>();

//    public static <T> void set(TypeRef<T> typeRef, ITypeHandler<T> typeHandler)
//    {
//        cache.put(typeRef.getActualType(), typeHandler);
//    }

    public static <T> void set(ITypeHandler<T> typeHandler)
    {
        Type actualType = typeHandler.getActualType();
        warpBaseType(actualType, typeHandler);
        cache.put(actualType, typeHandler);
    }

    static
    {
        //varchar
        set(new CharTypeHandler());
        set(new StringTypeHandler());

        //number
        set(new ByteTypeHandler());
        set(new ShortTypeHandler());
        set(new IntTypeHandler());
        set(new LongTypeHandler());
        set(new BoolTypeHandler());
        set(new FloatTypeHandler());
        set(new DoubleTypeHandler());
        set(new BigIntegerTypeHandler());
        set(new BigDecimalTypeHandler());

        //datetime
        set(new DateTypeHandler());
        set(new UtilDateHandler());
        set(new TimeTypeHandler());
        set(new TimestampTypeHandler());
        set(new LocalDateTimeTypeHandler());
        set(new LocalDateTypeHandler());
        set(new LocalTimeTypeHandler());

        //other
        set(new URLTypeHandler());
    }

    public static <T> ITypeHandler<T> get(Type type)
    {
        ITypeHandler<T> iTypeHandler = (ITypeHandler<T>) cache.get(type);
        if (iTypeHandler == null)
        {
            return (ITypeHandler<T>) unKnowTypeHandler;
        }
        return iTypeHandler;
    }

    private static void warpBaseType(Type actualType, ITypeHandler<?> typeHandler)
    {
        if (actualType == Character.class)
        {
            cache.put(char.class, typeHandler);
        }
        else if (actualType == Byte.class)
        {
            cache.put(byte.class, typeHandler);
        }
        else if (actualType == Short.class)
        {
            cache.put(short.class, typeHandler);
        }
        else if (actualType == Integer.class)
        {
            cache.put(int.class, typeHandler);
        }
        else if (actualType == Long.class)
        {
            cache.put(long.class, typeHandler);
        }
        else if (actualType == Float.class)
        {
            cache.put(float.class, typeHandler);
        }
        else if (actualType == Double.class)
        {
            cache.put(double.class, typeHandler);
        }
        else if (actualType == Boolean.class)
        {
            cache.put(boolean.class, typeHandler);
        }
    }
}
