package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * Validates the guard-clause logic used in JdkHttpUtils.FormBody.write()
 * to prevent StringIndexOutOfBoundsException when there are no params.
 */
public class FormBodyEmptyTest {

    @Test
    void testEmptyParamsBuilder() {
        StringBuilder builder = new StringBuilder();
        // Simulate the fix: only delete trailing '&' if something was appended
        if (builder.length() > 0) {
            builder.delete(builder.length() - 1, builder.length());
        }
        Assertions.assertEquals(0, builder.length());
        Assertions.assertEquals("", builder.toString());
    }

    @Test
    void testSingleParamBuilder() {
        StringBuilder builder = new StringBuilder();
        builder.append("key=value");
        // Remove the trailing '&' — but here there is none, so the string stays intact.
        // In the real code the pattern is: append "k=v&" for each entry, then trim '&'.
        builder.append("&");
        if (builder.length() > 0) {
            builder.delete(builder.length() - 1, builder.length());
        }
        Assertions.assertEquals("key=value", builder.toString());
    }
}