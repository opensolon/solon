package labs;

import org.junit.Test;

import java.net.URI;

/**
 * @author noear 2022/3/8 created
 */
public class UriTest {
    @Test
    public void test(){
        URI uri = URI.create("");
        System.out.println(uri);
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());

        assert uri.getHost() == null;
        assert "".equals(uri.getPath());
    }

    @Test
    public void test2(){
        URI uri = URI.create(null);
        System.out.println(uri);
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());

        assert "".equals(uri.getHost());
        assert uri.getPath() == null;
    }
}
