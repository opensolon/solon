package demo.httputils;

import okhttp3.OkHttpClient;
import org.noear.solon.net.http.HttpSslSupplier;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsFactory;

import java.net.Proxy;

/**
 * //重写后找个地方设置下：HttpConfiguration.setFactory(new OkHttpUtilsFactoryExt());
 */
public class OkHttpUtilsFactoryExt extends OkHttpUtilsFactory {
    @Override
    protected OkHttpClient getClient(Proxy proxy, HttpSslSupplier sslProvider) {
        //重写这个方法

        return super.getClient(proxy, sslProvider);
    }
}
