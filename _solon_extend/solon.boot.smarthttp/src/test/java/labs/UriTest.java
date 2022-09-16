package labs;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

/**
 * @author noear 2022/3/8 created
 */
public class UriTest {
    @Test
    @SuppressWarnings("ALL")
    public void test(){
        Assert.assertThrows(NullPointerException.class, () -> URI.create(null));

        URI uri = URI.create("");
        Assert.assertNotNull(uri);
        System.out.println(uri);
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());

        assert uri.getHost() == null;
        assert "".equals(uri.getPath());
    }
}
