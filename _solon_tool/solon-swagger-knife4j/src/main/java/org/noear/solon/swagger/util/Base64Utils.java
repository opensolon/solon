package org.noear.solon.swagger.util;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author noear 2022/4/13 created
 */
public class Base64Utils {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private Base64Utils() {
    }

    public static String encode(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    public static String encode(String value) {
        byte[] val = value.getBytes(UTF_8);
        return encode(val);
    }

    public static String encode(String value, String charsetName) {
        byte[] val = value.getBytes(Charset.forName(charsetName));
        return encode(val);
    }

    public static byte[] decode(String value) {
        return Base64.getDecoder().decode(value);
    }

    public static String decodeToStr(String value) {
        byte[] decodedValue = decode(value);
        return new String(decodedValue, UTF_8);
    }

    public static String decodeToStr(String value, String charsetName) {
        byte[] decodedValue = decode(value);
        return new String(decodedValue, Charset.forName(charsetName));
    }

    private static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            Class.forName(className, true, classLoader);
            return true;
        } catch (Throwable var3) {
            return false;
        }
    }
}
