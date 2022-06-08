package org.noear.solon.boot.vertx.http;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 1.8
 */
public class VertxHttpContext extends Context {
    HttpServerRequest request;
    HttpServerResponse response;

    VertxHttpContext(HttpServerRequest request){
        this.request = request;
        this.response = request.response();

    }
    @Override
    public Object request() {
        return request;
    }

    private String _ip;
    @Override
    public String ip() {
        if (_ip == null) {
            _ip = request.remoteAddress().host();
        }

        return _ip;
    }

    @Override
    public String method() {
        return request.method().name();
    }

    @Override
    public String protocol() {
        if (request.isSSL()) {
            return "https";
        } else {
            return "http";
        }
    }

    private URI _uri;

    @Override
    public URI uri() {
        if (_uri == null) {
            _uri = URI.create(url());
        }
        return _uri;
    }

    @Override
    public String path() {
        return uri().getPath();
    }

    private String _url;
    @Override
    public String url() {
        if(_url == null){
            _url = request.absoluteURI();
        }

        return _url;
    }

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public String contentType() {
        return null;
    }

    @Override
    public String queryString() {
        return null;
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return null;
    }

    @Override
    public String[] paramValues(String name) {
        return new String[0];
    }

    @Override
    public String param(String name) {
        return null;
    }

    @Override
    public String param(String name, String def) {
        return null;
    }

    @Override
    public NvMap paramMap() {
        return null;
    }

    @Override
    public Map<String, List<String>> paramsMap() {
        return null;
    }

    @Override
    public List<UploadedFile> files(String name) throws Exception {
        return null;
    }

    @Override
    public NvMap cookieMap() {
        return null;
    }

    @Override
    public NvMap headerMap() {
        return null;
    }

    @Override
    public Object response() {
        return response;
    }

    @Override
    protected void contentTypeDoSet(String contentType) {

    }

    @Override
    public void output(byte[] bytes) {

    }

    @Override
    public void output(InputStream stream) {

    }

    @Override
    public OutputStream outputStream() throws IOException {
        return null;
    }

    @Override
    public void headerSet(String name, String val) {

    }

    @Override
    public void headerAdd(String name, String val) {

    }

    @Override
    public void cookieSet(String name, String val, String domain, String path, int maxAge) {

    }

    @Override
    public void redirect(String url) {

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

    }
}
