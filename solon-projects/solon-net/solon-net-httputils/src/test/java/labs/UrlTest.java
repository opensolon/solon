package labs;

import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsImpl;

import java.nio.charset.Charset;

/**
 * @author noear 2024/10/4 created
 */
public class UrlTest {
    public static void main(String[] args) throws Exception {
        String url = "http://localhost:8080/demo2/mapping/e/{p_q}/{obj}/{id}?a=中";

        String url2 = JdkHttpUtilsImpl.urlRebuild(url, Charset.defaultCharset());

        System.out.println(url2);
    }
}
