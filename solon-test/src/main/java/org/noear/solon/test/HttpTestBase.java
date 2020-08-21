package org.noear.solon.test;

import org.noear.solon.XApp;

public class HttpTestBase {
    public HttpUtils path(String path) {
        return http("http://localhost:" + XApp.global().port() + path);
    }

    public HttpUtils http(String url) {
        return HttpUtils.http(url);
    }
}
