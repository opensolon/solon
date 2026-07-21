package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tests cookie parsing edge cases by replicating the name=value extraction logic
 * found in JdkHttpResponse.cookiesInit().
 */
public class CookiesParseTest {

    /**
     * Replicates the cookie parsing logic from JdkHttpResponse.cookiesInit().
     * Each entry in the input list is a single Set-Cookie header value.
     *
     * @param setCookieValues list of raw Set-Cookie header values
     * @return a map of cookie name -> value (last one wins for duplicates)
     */
    private Map<String, String> parseCookies(java.util.List<String> setCookieValues) {
        Map<String, String> cookies = new LinkedHashMap<>();
        for (String kvStr : setCookieValues) {
            int eqIdx = kvStr.indexOf('=');
            if (eqIdx < 0) continue;
            int smIdx = kvStr.indexOf(';', eqIdx);
            String key = kvStr.substring(0, eqIdx);
            String value = smIdx > 0 ? kvStr.substring(eqIdx + 1, smIdx) : kvStr.substring(eqIdx + 1);
            cookies.put(key, value);
        }
        return cookies;
    }

    @Test
    public void testCookieWithoutEquals() {
        // A Set-Cookie value with no '=' should be skipped entirely
        java.util.List<String> input = java.util.Collections.singletonList("httponly");
        Map<String, String> cookies = parseCookies(input);
        Assertions.assertTrue(cookies.isEmpty(), "Cookie without '=' should be skipped");
    }

    @Test
    public void testCookieWithSemicolon() {
        // Set-Cookie: session=abc123; Path=/; HttpOnly
        java.util.List<String> input = java.util.Collections.singletonList("session=abc123; Path=/; HttpOnly");
        Map<String, String> cookies = parseCookies(input);
        Assertions.assertEquals("abc123", cookies.get("session"));
        Assertions.assertEquals(1, cookies.size(), "Only the name=value pair before ';' should be extracted");
    }

    @Test
    public void testCookieSimple() {
        // Set-Cookie: token=xyz
        java.util.List<String> input = java.util.Collections.singletonList("token=xyz");
        Map<String, String> cookies = parseCookies(input);
        Assertions.assertEquals("xyz", cookies.get("token"));
        Assertions.assertEquals(1, cookies.size());
    }
}