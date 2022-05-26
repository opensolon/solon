package org.noear.solon.extend.aspect.asm;

import org.noear.solon.core.JarClassLoader;

/**
 * @author noear
 * @since 1.5
 */
public class AsmProxyClassLoader extends ClassLoader {
    private static AsmProxyClassLoader global = new AsmProxyClassLoader();
    public static AsmProxyClassLoader global() {
        return global;
    }

    public AsmProxyClassLoader() {
        super(JarClassLoader.global());
    }

    public Class<?> transfer2Class(byte[] bytes) {
        return defineClass(null, bytes, 0, bytes.length);
    }
}
