package org.noear.water.utils;

public class AssertUtils {
    private AssertUtils() {
    }

    public static void notNull(Object obj, String varName) {
        if (obj == null) {
            throw new IllegalArgumentException("Object must not be null @" + varName);
        }
    }

    public static void isTrue(boolean val, String varName) {
        if (!val) {
            throw new IllegalArgumentException("Must be true @" + varName);
        }
    }

    public static void isFalse(boolean val, String varName) {
        if (val) {
            throw new IllegalArgumentException("Must be false @" + varName);
        }
    }

    public static void noNullElements(Object[] objects, String varName) {
        Object[] var2 = objects;
        int var3 = objects.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Object obj = var2[var4];
            if (obj == null) {
                throw new IllegalArgumentException("Array must not contain any null objects @" + varName);
            }
        }

    }

    public static void notEmpty(String string, String varName) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("String must not be empty @" + varName);
        }
    }

    public static void fail(String msg) {
        throw new IllegalArgumentException(msg);
    }
}
