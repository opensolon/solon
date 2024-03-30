package org.noear.solon.boot.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.Utils;
import org.noear.solon.boot.web.Constants;
import org.noear.solon.boot.web.WebContextBase;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IgnoreCaseMap;
import org.noear.solon.core.util.RunUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.7
 */
public class VxHttpContext extends WebContextBase {
    static final Logger log = LoggerFactory.getLogger(VxHttpContext.class);

    private HttpServerRequest _request;
    private HttpServerResponse _response;

    private boolean _isAsync;
    private long _asyncTimeout = 30000L;//默认30秒
    private CompletableFuture<Object> _asyncFuture;
    private List<ContextAsyncListener> _asyncListeners = new ArrayList<>();

    protected HttpServerRequest innerGetRequest() {
        return _request;
    }

    protected HttpServerResponse innerGetResponse() {
        return _response;
    }

    protected boolean innerIsAsync() {
        return _isAsync;
    }

    protected List<ContextAsyncListener> innerAsyncListeners() {
        return _asyncListeners;
    }

    public VxHttpContext(HttpServerRequest request, HttpServerResponse response){
        this._request = request;
        this._response = response;

        _filesMap = new HashMap<>();
    }

    private boolean _loadMultipartFormData = false;

    private void loadMultipartFormData() throws IOException {
        if (_loadMultipartFormData) {
            return;
        } else {
            _loadMultipartFormData = true;
        }

        //文件上传需要
        if (isMultipartFormData()) {
            //MultipartUtil.buildParamsAndFiles(this, _filesMap);
        }
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String remoteIp() {
        return _request.remoteAddress().host();
    }

    @Override
    public int remotePort() {
        return _request.remoteAddress().port();
    }

    @Override
    public String method() {
        return _request.method().name();
    }

    @Override
    public String protocol() {
        return "http";
    }

    private URI _uri;
    @Override
    public URI uri() {
        if(_uri == null){
            _uri = URI.create(url());
        }

        return _uri;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String url() {
        return _request.absoluteURI();
    }


    private int contentLength = -2;
    @Override
    public long contentLength() {
        if (contentLength > -2) {
            return contentLength;
        } else {
            String tmp = _request.getHeader("Content-Length");
            if (Utils.isEmpty(tmp)) {
                contentLength = -1;
            } else {
                contentLength = Integer.parseInt(tmp);
            }

            return contentLength;
        }
    }

    @Override
    public String queryString() {
        return _request.query();
    }

    private InputStream bodyAsStream;
    @Override
    public InputStream bodyAsStream() throws IOException {
        if (bodyAsStream != null) {
            return bodyAsStream;
        } else {
            CompletableFuture<Buffer> future = new CompletableFuture<>();
            _request.body(r -> {
                if (r.succeeded()) {
                    future.complete(r.result());
                } else {
                    future.completeExceptionally(r.cause());
                }
            });
            Buffer buffer;
            try {
                buffer = future.get();
            } catch (Throwable e) {
                throw new IOException(e);
            }

            bodyAsStream = new ByteArrayInputStream(buffer.getBytes());
            return bodyAsStream;
        }
    }

    private NvMap _paramMap;
    @Override
    public NvMap paramMap() {
        if (_paramMap == null) {
            _paramMap = new NvMap();

            try {
                if (autoMultipart()) {
                    loadMultipartFormData();
                }

                for (Map.Entry<String, String> entry : _request.params()) {
                    _paramMap.put(entry.getKey(), entry.getValue());
                }

                for (Map.Entry<String, String> entry : _request.formAttributes()) {
                    _paramMap.put(entry.getKey(), entry.getValue());
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        return _paramMap;
    }

    private Map<String, List<String>> _paramsMap;
    @Override
    public Map<String, List<String>> paramsMap() {
        if (_paramsMap == null) {
            _paramsMap = new LinkedHashMap<>();

            try {
                if (autoMultipart()) {
                    loadMultipartFormData();
                }
                for (String name : _request.params().names()) {
                    _paramsMap.computeIfAbsent(name, k -> new ArrayList<>()).addAll(_request.params().getAll(name));
                }

                for (String name : _request.formAttributes().names()) {
                    _paramsMap.computeIfAbsent(name, k -> new ArrayList<>()).addAll(_request.formAttributes().getAll(name));
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        return _paramsMap;
    }

    @Override
    public Map<String, List<UploadedFile>> filesMap() throws IOException {
        if (isMultipartFormData()) {
            loadMultipartFormData();

            return _filesMap;
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public NvMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new NvMap();

            for (Cookie c1 : _request.cookies()) {
                _cookieMap.put(c1.getName(), c1.getValue());
            }
        }

        return _cookieMap;
    }
    private NvMap _cookieMap;

    @Override
    public NvMap headerMap() {
        if (_headerMap == null) {
            _headerMap = new NvMap();

            for (Map.Entry<String, String> kv : _request.headers()) {
                _headerMap.put(kv.getKey(), kv.getValue());
            }
        }

        return _headerMap;
    }

    private NvMap _headerMap;

    @Override
    public Map<String, List<String>> headersMap() {
        if (_headersMap == null) {
            _headersMap = new IgnoreCaseMap<>();

            for (String name : _request.headers().names()) {
                _headersMap.put(name, new ArrayList<>(_request.headers().getAll(name)));
            }
        }

        return _headersMap;
    }
    private Map<String, List<String>> _headersMap;

    @Override
    public Object response() {
        return _response;
    }

    @Override
    protected void contentTypeDoSet(String contentType) {
        if (charset != null) {
            if (contentType.indexOf(";") < 0) {
                headerSet(Constants.HEADER_CONTENT_TYPE, contentType + ";charset=" + charset);
                return;
            }
        }

        headerSet(Constants.HEADER_CONTENT_TYPE, contentType);
    }

    @Override
    public void output(byte[] bytes) {

    }

    @Override
    public void output(InputStream stream) {

    }

    @Override
    public OutputStream outputStream() throws IOException {
        return _response.send();
    }

    @Override
    public void headerSet(String name, String val) {

    }

    @Override
    public void headerAdd(String name, String val) {

    }

    @Override
    public String headerOfResponse(String name) {
        return null;
    }

    @Override
    public void cookieSet(String name, String val, String domain, String path, int maxAge) {

    }

    @Override
    public void redirect(String url, int code) {

    }

    @Override
    public int status() {
        return 0;
    }

    @Override
    protected void statusDoSet(int status) {

    }

    @Override
    public void flush() throws IOException {
        if (_allows_write) {
            outputStream().flush();
        }
    }

    @Override
    public void close() throws IOException {
        _response.close();
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener) {
        if (_isAsync == false) {
            _isAsync = true;

            if(listener != null) {
                _asyncListeners.add(listener);
            }

            if (timeout != 0) {
                _asyncTimeout = timeout;
            }

            if (_asyncTimeout > 0) {
                RunUtil.delay(() -> {
                    for (ContextAsyncListener listener1 : _asyncListeners) {
                        try {
                            listener1.onTimeout(this);
                        } catch (IOException e) {
                            log.warn(e.getMessage(), e);
                        }
                    }
                }, _asyncTimeout);
            }
        }
    }


    @Override
    public void asyncComplete() throws IOException {
        if (_isAsync) {
            try {
                innerCommit();
            } finally {
                _asyncFuture.complete(this);
            }
        }
    }

    @Override
    protected void innerCommit() throws IOException {
        if (getHandled() || status() >= 200) {
            sendHeaders(true);
        } else {
            status(404);
            sendHeaders(true);
        }
    }

    private boolean _headers_sent = false;
    private boolean _allows_write = true;

    private void sendHeaders(boolean isCommit) throws IOException {
        if (!_headers_sent) {
            _headers_sent = true;

            if ("HEAD".equals(method())) {
                _allows_write = false;
            }

            if (sessionState() != null) {
                sessionState().sessionPublish();
            }

            _response.setHttpStatus(HttpStatus.valueOf(status()));

            if (isCommit || _allows_write == false) {
                _response.setContentLength(0);
            }
        }
    }
}
