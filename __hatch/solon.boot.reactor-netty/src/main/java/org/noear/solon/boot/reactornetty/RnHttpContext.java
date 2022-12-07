package org.noear.solon.boot.reactornetty;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import org.noear.solon.Utils;
import org.noear.solon.boot.web.ContextBase;
import org.noear.solon.boot.web.RedirectUtils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.NvMap;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaderNames.SET_COOKIE;
import static io.netty.handler.codec.http.HttpHeaderNames.COOKIE;


public class RnHttpContext extends ContextBase {
    private final HttpServerRequest _request;
    private final HttpServerResponse _response;
    private final HttpRequestParser _request_parse;
    public RnHttpContext(HttpServerRequest request, HttpServerResponse response, HttpRequestParser request_parse) {
        _request = request;
        _response = response;

        try {
            _request_parse = request_parse.parse();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Object request() {
        return _request;
    }

    private String _ip;
    @Override
    public String ip() {
        if(_ip == null) {
            _ip = header("X-Forwarded-For");
            if (_ip == null) {
                InetSocketAddress insocket =_request.remoteAddress();
                _ip = insocket.getAddress().getHostAddress();
            }
        }

        return _ip;
    }

    @Override
    public String method() {
        return _request.method().name();
    }

    @Override
    public String protocol() {
        return _request.version().protocolName();
    }

    private URI _uri;
    @Override
    public URI uri() {
        if(_uri == null){
            _uri = URI.create(url());
        }
        return _uri;
    }

    private String _url;
    @Override
    public String url() {
        if (_url == null) {
            _url = _request.uri();

            if (_url != null && _url.startsWith("/")) {
                _url = _request.scheme()+"://" + header("Host") + _url;
            }
        }

        return _url;
    }

    @Override
    public long contentLength() {
        return _request.receiveContent().count().block();
    }

    @Override
    public String contentType() {
        return header("Content-Type");
    }

    @Override
    public String queryString() {
        return null;
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.receive().asInputStream().blockFirst();
    }

    @Override
    public String[] paramValues(String key) {
        List<String> list = paramsMap().get(key);
        if(list == null){
            return null;
        }

        return list.toArray(new String[list.size()]);
    }

    @Override
    public String param(String key) {
        //要充许为字符串
        //默认不能为null
        return paramMap().get(key);
    }

    @Override
    public String param(String key, String def) {
        String tmp = paramMap().get(key);
        if(tmp == null) {
            return def;
        }else{
            return tmp;
        }
    }

    private NvMap _paramMap;
    @Override
    public NvMap paramMap() {
        if(_paramMap == null){
            _paramMap = new NvMap();

            _request_parse.parmMap.forEach((k,l)->{
                if(l.size() > 0){
                    _paramMap.put(k,l.get(0));
                }
            });
        }

        return _paramMap;
    }

    @Override
    public Map<String, List<String>> paramsMap() {
        return _request_parse.parmMap;
    }

    @Override
    public List<UploadedFile> files(String key) throws Exception {
        return _request_parse.fileMap.get(key);
    }

    private NvMap _cookieMap;
    @Override
    public NvMap cookieMap() {
        if(_cookieMap == null){
            _cookieMap = new NvMap();

            String _cookieMapStr = _request.requestHeaders().get(COOKIE);
            if (_cookieMapStr != null) {
                Set<Cookie> tmp = ServerCookieDecoder.LAX.decode(_cookieMapStr);

                for(Cookie c1 :tmp){
                    _cookieMap.put(c1.name(),c1.value());
                }
            }
        }

        return _cookieMap;
    }

    private NvMap _headerMap;
    @Override
    public NvMap headerMap() {
        if(_headerMap == null) {
            _headerMap = new NvMap();
            HttpHeaders headers = _request.requestHeaders();

            for(Map.Entry<String, String> kv : headers){
                _headerMap.put(kv.getKey(), kv.getValue());
            }
        }

        return _headerMap;
    }

    @Override
    public Object response() {
        return _response;
    }

    @Override
    protected void contentTypeDoSet(String contentType) {
        if (charset != null) {
            if (contentType.indexOf(";") < 0) {
                headerSet("Content-Type", contentType + ";charset=" + charset);
                return;
            }
        }

        headerSet("Content-Type", contentType);
    }

    @Override
    public OutputStream outputStream() throws IOException{
        return _outputStream;
    }

    @Override
    public void output(byte[] bytes) {
        try {
            OutputStream out = outputStream();

            out.write(bytes);
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            OutputStream out = outputStream();

            Utils.transferTo(stream, out);
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    protected ByteBufOutputStream _outputStream = new ByteBufOutputStream(Unpooled.buffer());

    @Override
    public void headerSet(String key, String val) {
        _response.header(key,val);
    }

    @Override
    public void headerAdd(String key, String val) {
        _response.addHeader(key,val);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append("=").append(val).append(";");

        if (Utils.isNotEmpty(path)) {
            sb.append("path=").append(path).append(";");
        }

        if (maxAge >= 0) {
            sb.append("max-age=").append(maxAge).append(";");
        }

        if (Utils.isNotEmpty(domain)) {
            sb.append("domain=").append(domain.toLowerCase()).append(";");
        }

        _response.addHeader(SET_COOKIE, sb.toString());
    }

    @Override
    public void redirect(String url, int code) {
        url = RedirectUtils.getRedirectPath(url);

        headerSet("Location", url);
        statusDoSet(code);
    }

    @Override
    public int status() {
        return _status;
    }

    private int _status = 200;

    @Override
    protected void statusDoSet(int status) {
        _status = status;
        _response.status(status);
    }

    @Override
    public void flush() throws IOException {
        //不用实现
    }
}
