package org.noear.solon.core.wrap;

import org.noear.solon.lang.Nullable;

import java.lang.reflect.Method;

/**
 * 方法键（一般用于 map、cache key）
 *
 * @author noear
 * @since 2.8
 */
public class MethodKey implements Comparable<MethodKey> {
    private final Method method;
    @Nullable
    private final Class<?> targetClass;

    public MethodKey(Method method, @Nullable Class<?> targetClass) {
        this.method = method;
        this.targetClass = targetClass;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof MethodKey)) {
            return false;
        } else {
            MethodKey other = (MethodKey) o;

            if (this.method.equals(other.method)) {
                if (this.targetClass == other.targetClass) {
                    return true;
                } else if (this.targetClass != null && other.targetClass != null) {
                    return this.targetClass.equals(other.targetClass);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        if (targetClass == null) {
            return method.hashCode();
        } else {
            return method.hashCode() * 31 + targetClass.hashCode();
        }
    }

    @Override
    public String toString() {
        if (targetClass == null) {
            return method.toString();
        } else {
            return method.toString() + " on " + targetClass.getName();
        }
    }

    @Override
    public int compareTo(MethodKey o) {
        int result = this.method.getName().compareTo(o.method.getName());

        if (result == 0) {
            result = this.method.toString().compareTo(o.method.toString());
            if (result == 0 && this.targetClass != null && o.targetClass != null) {
                result = this.targetClass.getName().compareTo(o.targetClass.getName());
            }
        }

        return result;
    }
}