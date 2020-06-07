package org.noear.solon.extend.uapi;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import org.noear.solon.core.XMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * UAPI上下文（为扩展上下文提供基类）
 * */
public class UApiContext extends XContext {
    private XContext real;
    public UApiContext(XContext ctx){
        this.real = ctx;
    }

    @Override
    public Object request() {
        return real.request();
    }

    @Override
    public String ip() {
        return real.ip();
    }

    @Override
    public String method() {
        return real.method();
    }

    @Override
    public String protocol() {
        return real.protocol();
    }

    @Override
    public URI uri() {
        return real.uri();
    }

    @Override
    public String path() {
        return real.path();
    }

    @Override
    public String url() {
        return real.url();
    }

    @Override
    public long contentLength() {
        return real.contentLength();
    }

    @Override
    public String contentType() {
        return real.contentType();
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return real.bodyAsStream();
    }

    @Override
    public String[] paramValues(String key) {
        return real.paramValues(key);
    }

    @Override
    public String param(String key) {
        return real.param(key);
    }

    @Override
    public String param(String key, String def) {
        return real.param(key,def);
    }

    @Override
    public XMap paramMap() {
        return real.paramMap();
    }

    @Override
    public Map<String, List<String>> paramsMap() {
        return real.paramsMap();
    }

    @Override
    public List<XFile> files(String key) throws Exception {
        return real.files(key);
    }

    @Override
    public String cookie(String key) {
        return real.cookie(key);
    }

    @Override
    public String cookie(String key, String def) {
        return real.cookie(key,def);
    }

    @Override
    public XMap cookieMap() {
        return real.cookieMap();
    }

    @Override
    public String header(String key) {
        return real.header(key);
    }

    @Override
    public String header(String key, String def) {
        return real.header(key,def);
    }

    @Override
    public XMap headerMap() {
        return real.headerMap();
    }

    @Override
    public Object response() {
        return real.response();
    }

    @Override
    public void charset(String charset) {
        real.charset(charset);
    }

    @Override
    protected void contentTypeDoSet(String contentType) {

    }

    @Override
    public void output(String str) {
        real.output(str);
    }

    @Override
    public void output(InputStream stream) {
        real.output(stream);
    }

    @Override
    public OutputStream outputStream() throws IOException {
        return real.outputStream();
    }

    @Override
    public void headerSet(String key, String val) {
        real.headerSet(key,val);
    }

    @Override
    public void headerAdd(String key, String val) {
        real.headerAdd(key,val);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        real.cookieSet(key,val,domain,path,maxAge);
    }

    @Override
    public void redirect(String url) {
        real.redirect(url);
    }

    @Override
    public void redirect(String url, int code) {
        real.redirect(url, code);
    }

    @Override
    public int status() {
        return real.status();
    }

    @Override
    public void status(int status) {
        real.status(status);
    }

    //
    //
    //

    public String uapiName(){
        return attr("api");
    }
}
