package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.net.http.HttpCallback;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.AbstractHttpUtils;

import java.io.IOException;

/**
 * @author noear
 * @since 3.0
 */
public class JdkHttpUtilsImpl extends AbstractHttpUtils implements HttpUtils {
    public JdkHttpUtilsImpl(String url) {
        super(url);
    }

    @Override
    protected HttpResponse execDo(String mothod, HttpCallback callback) throws IOException {
        return null;
    }
}
