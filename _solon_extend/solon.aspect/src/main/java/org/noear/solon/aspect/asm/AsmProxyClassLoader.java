package org.noear.solon.aspect.asm;

/**
 * @author noear
 * @since 1.5
 */
public class AsmProxyClassLoader extends ClassLoader {

    public AsmProxyClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public Class<?> transfer2Class(byte[] bytes) {
        return defineClass(null, bytes, 0, bytes.length);
    }
}
