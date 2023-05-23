package io.swagger.solon.api;

import java.nio.charset.Charset;
import java.util.Base64;

public class Base64Utils {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static byte[] decode(String value) {
        return Base64.getDecoder().decode(value);
    }

    public static String decodeToStr(String value) {
        byte[] decodedValue = decode(value);
        return new String(decodedValue, UTF_8);
    }
}
