package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.core.util.KeyValues;
import org.noear.solon.net.http.HttpCallback;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.AbstractHttpUtils;
import org.noear.solon.net.http.impl.HttpTimeout;
import org.noear.solon.net.http.impl.HttpUploadFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author noear
 * @since 3.0
 */
public class JdkHttpUtilsImpl extends AbstractHttpUtils implements HttpUtils {
    private final HttpClient _client;

    public JdkHttpUtilsImpl(String url) {
        this(url, null);
    }

    public JdkHttpUtilsImpl(String url, HttpClient client) {
        super(url);

        _timeout = new HttpTimeout(60);

        if (client == null) {
            _client = HttpClient.buildHttpClient();
        } else {
            _client = client;
        }
    }

    @Override
    protected HttpResponse execDo(String method, HttpCallback callback) throws IOException {
        HttpClient.Request _builder = _client.buildRequest(_url).setIfEncodeUrl(true);

        if (_timeout != null) {
            _builder.setConnectTimeout(_timeout.connectTimeout * 1000);
            _builder.setReadTimeout(_timeout.readTimeout * 1000);
        }

        if (_headers != null) {
            for (KeyValues<String> kv : _headers) {
                for (String val : kv.getValues()) {
                    _builder.addHeader(kv.getKey(), val);
                }
            }
        }

        if (_cookies != null) {
            _builder.addHeader("Cookie", getRequestCookieString(_cookies));
        }

        if (_bodyRaw != null) {
            _builder.setParam(HttpClient.Params.ofRaw(_bodyRaw.contentType, _bodyRaw.content));
        } else {
            if (_multipart) {
                HttpClient.Params.ParamFormData _form_builer = HttpClient.Params.ofFormData(_charset);

                if (_files != null) {
                    for (KeyValues<HttpUploadFile> kv : _files) {
                        for (HttpUploadFile val : kv.getValues()) {
                            _form_builer.addFile(kv.getKey(), val.fileName, val.fileStream.content, val.fileStream.contentType);
                        }
                    }
                }

                for (KeyValues<String> kv : _params) {
                    for (String val : kv.getValues()) {
                        _form_builer.add(kv.getKey(), val);
                    }
                }

                _builder.setParam(_form_builer);

            } else if (_params != null) {
                HttpClient.Params.ParamForm _form_builer = HttpClient.Params.ofForm(_charset);

                for (KeyValues<String> kv : _params) {
                    for (String val : kv.getValues()) {
                        _form_builer.add(kv.getKey(), val);
                    }
                }

                _builder.setParam(_form_builer);
            } else {
                //HEAD 可以为空
            }
        }

        //
        _builder.setMethod(HttpClient.Method.valueOf(method));
        HttpClient.Response<InputStream> _response =  _builder.execute(HttpClient.BodyHandlers.ofInputStream());

        return new JdkHttpResponseImpl(_response);
    }
}
