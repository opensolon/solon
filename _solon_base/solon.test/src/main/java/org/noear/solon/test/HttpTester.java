package org.noear.solon.test;

import org.noear.solon.Solon;

/**
 * Http 接口测试器
 *
 * @author noear
 * @since 2.2
 */
public class HttpTester {
    public boolean enablePrint() {
        return true;
    }


    /**
     * 请求当前服务
     */
    public HttpUtils path(String path) {
        return path(Solon.cfg().serverPort(), path);
    }

    /**
     * 请求本机服务
     */
    public HttpUtils path(int port, String path) {
        return http("http://localhost:" + port + path);
    }

    /**
     * 请求本机服务
     */
    public HttpUtils http(int port) {
        return HttpUtils.http("http://localhost:" + port).enablePrintln(enablePrint());
    }

    /**
     * 请求服务
     */
    public HttpUtils http(String url) {
        return HttpUtils.http(url).enablePrintln(enablePrint());
    }
}
