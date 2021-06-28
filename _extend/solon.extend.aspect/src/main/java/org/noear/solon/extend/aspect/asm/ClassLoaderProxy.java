package org.noear.solon.extend.aspect.asm;

import org.noear.solon.core.JarClassLoader;

/**
 * @author noear
 * @since 1.5
 */
public class ClassLoaderProxy extends ClassLoader {
    private static ClassLoaderProxy global = new ClassLoaderProxy();
    public static ClassLoaderProxy global() {
        return global;
    }

    public ClassLoaderProxy() {
        super(JarClassLoader.global());
    }

    public Class<?> transfer2Class(byte[] bytes) {
        return defineClass(null, bytes, 0, bytes.length);
    }
}
