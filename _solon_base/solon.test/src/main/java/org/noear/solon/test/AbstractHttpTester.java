package org.noear.solon.test;

import org.noear.solon.Solon;

/**
 * Http 接口测试器
 *
 * @author noear
 * @since 2.1
 */
public abstract class AbstractHttpTester {
    public boolean enablePrint() {
        return true;
    }

    /**
     * 方便请求当前服务
     */
    public HttpUtils path(String path) {
        return http("http://localhost:" + Solon.cfg().serverPort() + path);
    }

    /**
     * 方便请求本机服务
     */
    public HttpUtils http(int port) {
        return HttpUtils.http("http://localhost:" + port).enablePrintln(enablePrint());
    }

    public HttpUtils http(String url) {
        return HttpUtils.http(url).enablePrintln(enablePrint());
    }
}
