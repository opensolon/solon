package org.noear.solon.core.runtime;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Aot 收集器
 *
 * @author noear
 * @since  2.2
 */
public class AotCollector{
    private final Set<Class<?>> entityTypes = new LinkedHashSet<>();
    private final Set<Class<?>> jdkProxyTypes = new LinkedHashSet<>();

    public Set<Class<?>> getEntityTypes() {
        return entityTypes;
    }

    public Set<Class<?>> getJdkProxyTypes() {
        return jdkProxyTypes;
    }

    public void registerEntityType(Class<?> type, ParameterizedType genericType) {
        if (NativeDetector.isAotRuntime()) {
            if (type.getName().startsWith("java.") == false) {
                entityTypes.add(type);
            }

            if (genericType != null) {
                for (Type type1 : genericType.getActualTypeArguments()) {
                    if (type1 instanceof Class) {
                        if (type1.getTypeName().startsWith("java.") == false) {
                            entityTypes.add((Class<?>) type1);
                        }
                    }
                }
            }
        }
    }

    public void registerJdkProxyType(Class<?> type, Object target) {
        if (NativeDetector.isAotRuntime()) {
            if (Proxy.isProxyClass(target.getClass())) {
                if (type.getName().startsWith("java.") == false) {
                    jdkProxyTypes.add(type);
                }
            }
        }
    }

    public void clear() {
        entityTypes.clear();
        jdkProxyTypes.clear();
    }
}
