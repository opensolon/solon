package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.boot.prop.GzipProps;

/**
 * @author noear 2023/9/27 created
 */
public class MimeTest {
    @Test
    public void test(){
        assert "a".equals(GzipProps.resolveMime("a;b"));
        assert "a".equals(GzipProps.resolveMime("a"));
    }

    @Test
    public void test2_a(){
        long start = System.currentTimeMillis();
        for (int i=0; i< 1000000; i++){
            "a".split(";");
            "a;b".split(";");
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void test2_b(){
        long start = System.currentTimeMillis();
        for (int i=0; i< 1000000; i++){
            GzipProps.resolveMime("a");
            GzipProps.resolveMime("a;b");
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
