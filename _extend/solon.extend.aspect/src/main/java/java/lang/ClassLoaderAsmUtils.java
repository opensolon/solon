package java.lang;

/**
 * @author noear
 * @since 1.5
 */
public class ClassLoaderAsmUtils {
    public static Class<?> transfer2Class(ClassLoader classLoader, byte[] bytes) {
        return classLoader.defineClass(null, bytes, 0, bytes.length);
    }
}
