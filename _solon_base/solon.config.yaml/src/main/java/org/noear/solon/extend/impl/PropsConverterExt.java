package org.noear.solon.extend.impl;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.solon.Utils;
import org.noear.solon.core.PropsConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Properties;

/**
 * @author noear
 * @since 1.6
 */
public class PropsConverterExt extends PropsConverter {
    @Override
    public <T> T convert(Properties props, T target, Class<T> targetClz, Type targetType) {
        if (target == null) {
            try {
                //尝试用构造函数注入
                Constructor constructor = targetClz.getConstructor(Properties.class);
                if (constructor != null) {
                    return (T) constructor.newInstance(props);
                }
            } catch (NoSuchMethodException e) {
                //跳过
            } catch (Throwable e) {
                e = Utils.throwableUnwrap(e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }

            if(targetType == null){
                targetType = targetClz;
            }

            return ONode.loadObj(props, Feature.UseSetter).toObject(targetType);
        } else {
            return ONode.loadObj(props, Feature.UseSetter).bindTo(target);
        }
    }
}
