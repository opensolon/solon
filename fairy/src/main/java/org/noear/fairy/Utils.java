package org.noear.fairy;

class Utils {
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (Throwable ex) {
            return null;
        }
    }

    public static boolean hasClass(String className) {
        return loadClass(className) != null;
    }
}
