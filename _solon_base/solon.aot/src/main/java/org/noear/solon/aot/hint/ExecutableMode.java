package org.noear.solon.aot.hint;

import java.lang.reflect.Executable;

/**
 * Represent the need of reflection for a given {@link Executable}.
 *
 * @author Stephane Nicoll
 * @since 2.2
 */
public enum ExecutableMode {

    /**
     * Only retrieving the {@link Executable} and its metadata is required.
     * <p>仅仅需要检索某方法或者构造函数</p>
     */
    INTROSPECT,

    /**
     * Full reflection support is required, including the ability to invoke
     * the {@link Executable}.
     * <p>全部反射的能力，包括执行方法</p>
     */
    INVOKE;

    /**
     * Specify if this mode already includes the specified {@code other} mode.
     *
     * @param other the other mode to check
     * @return {@code true} if this mode includes the other mode
     */
    boolean includes(ExecutableMode other) {
        return (other == null || this.ordinal() >= other.ordinal());
    }

}
