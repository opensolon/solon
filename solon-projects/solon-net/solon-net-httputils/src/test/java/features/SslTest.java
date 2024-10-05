package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsImpl;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsImpl;

/**
 * @author noear 2024/10/4 created
 */
public class SslTest {
    @Test
    public void case11() throws Exception {
        String html = new JdkHttpUtilsImpl("https://solon.noear.org/").get();
        System.out.println(html);

        assert html != null;
        assert html.contains("Solon");
    }

    @Test
    public void case12() throws Exception {
        String html = new JdkHttpUtilsImpl("https://www.bilibili.com/").get();
        System.out.println(html);

        assert html != null;
        assert html.contains("bilibili");
    }

    @Test
    public void case21() throws Exception {
        String html = new OkHttpUtilsImpl("https://solon.noear.org/").get();
        System.out.println(html);

        assert html != null;
        assert html.contains("Solon");
    }

    @Test
    public void case22() throws Exception {
        String html = new OkHttpUtilsImpl("https://www.bilibili.com/").get();
        System.out.println(html);

        assert html != null;
        assert html.contains("bilibili");
    }
}
