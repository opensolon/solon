package org.noear.solon.aot.hint;

import org.noear.solon.core.util.ReflectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用来描述 {@link Method} 或者 {@link Constructor}.
 *
 * @author songyinyin
 * @since 2.2
 */
public class ExecutableHint {

    private String name;

    private List<String> parameterTypes;

    private ExecutableMode mode;

    public String getName() {
        return name;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public ExecutableMode getMode() {
        return mode;
    }

    public ExecutableHint(String name, Class<?>[] classes, ExecutableMode mode) {
        this.name = name;
        this.parameterTypes = classes == null ? null : Arrays.stream(classes).map(ReflectUtil::getClassName).collect(Collectors.toList());
        this.mode = mode;
    }

    public ExecutableHint(String name, List<String> parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutableHint that = (ExecutableHint) o;
        return Objects.equals(name, that.name) && Objects.equals(parameterTypes, that.parameterTypes) && mode == that.mode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameterTypes, mode);
    }
}
