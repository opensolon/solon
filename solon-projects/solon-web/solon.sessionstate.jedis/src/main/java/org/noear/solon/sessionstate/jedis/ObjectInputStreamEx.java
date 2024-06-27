package org.noear.solon.sessionstate.jedis;

import org.noear.solon.core.AppClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * ObjectInputStream 增加类加载控制
 *
 * @author noear
 * @since 2.8
 */
public class ObjectInputStreamEx extends ObjectInputStream {
    private ClassLoader loader;

    public ObjectInputStreamEx(ClassLoader loader, InputStream in) throws IOException {
        super(in);

        if (loader == null) {
            this.loader = AppClassLoader.global();
        } else {
            this.loader = loader;
        }
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        try {
            return Class.forName(name, false, loader);
        } catch (ClassNotFoundException ex) {
            throw ex;
        }
    }
}