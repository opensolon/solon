package org.noear.solon.net.http.impl.jdk;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

/**
 * @author noear 2024/10/4 created
 */
public class UrlRebuildTest {
    @Test
    public void case1() throws Exception {
        String url = "http://localhost:8080/demo2/mapping/e/{p_q}/{obj}/{id}?a=中";

        String url2 = ((JdkHttpUtils) JdkHttpUtilsFactory.getInstance().http(url)).urlRebuild(null, url, Charset.defaultCharset());

        System.out.println(url2);

        assert "http://localhost:8080/demo2/mapping/e/%7Bp_q%7D/%7Bobj%7D/%7Bid%7D?a=%E4%B8%AD".equals(url2);
    }
}
