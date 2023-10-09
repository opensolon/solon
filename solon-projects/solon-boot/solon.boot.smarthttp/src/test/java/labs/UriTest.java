package labs;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

/**
 * @author noear 2022/3/8 created
 */
public class UriTest {
    @Test
    @SuppressWarnings("ALL")
    public void test(){

        Assertions.assertThrows(NullPointerException.class, () -> URI.create(null));

        URI uri = URI.create("");
        Assertions.assertNotNull(uri);
        System.out.println(uri);
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());

        assert uri.getHost() == null;
        assert "".equals(uri.getPath());
    }
}
