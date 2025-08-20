package demo.httputils;

import org.noear.solon.annotation.Managed;
import org.noear.solon.net.http.HttpExtension;
import org.noear.solon.net.http.HttpSslSupplier;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.HttpSslSupplierDefault;

import javax.net.ssl.*;

/**
 *
 * @author noear 2025/8/8 created
 *
 */
@Managed
public class HttpSslExtensionDemo extends HttpSslSupplierDefault implements HttpExtension, HttpSslSupplier {
    // for HttpExtension
    @Override
    public void onInit(HttpUtils httpUtils, String url) {
        httpUtils.ssl(this);
    }

    @Override
    public SSLContext getSslContext() {
        return getAnySslContext();
    }
}
