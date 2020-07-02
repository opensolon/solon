package org.noear.solon.boot.rapidoid_http_fast;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import org.noear.solon.core.XMap;
import org.rapidoid.buffer.Buf;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class RhfContext extends XContext {

    private Channel _channel;
    private Buf _input;
    private RapidoidHelper _data;
    public RhfContext(Channel ctx, Buf buf, RapidoidHelper req) {
        _channel = ctx;
        _input = buf;
        _data = req;
    }


    @Override
    public Object request() {
        return _data;
    }

    private String _ip;
    @Override
    public String ip() {


        return _ip;
    }

    @Override
    public String method() {
        return null;
    }

    @Override
    public String protocol() {
        return null;
    }

    @Override
    public URI uri() {
        return null;
    }

    @Override
    public String path() {
        return null;
    }

    @Override
    public String url() {
        return null;
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
    public InputStream bodyAsStream() throws IOException {
        return null;
    }

    @Override
    public String[] paramValues(String key) {
        return new String[0];
    }

    @Override
    public String param(String key) {
        //要充许为字符串
        //默认不能为null
        return null;
    }

    @Override
    public String param(String key, String def) {
        return null;
    }

    @Override
    public XMap paramMap() {
        return null;
    }

    @Override
    public Map<String, List<String>> paramsMap() {
        return null;
    }

    @Override
    public List<XFile> files(String key) throws Exception {
        return null;
    }

    @Override
    public XMap cookieMap() {
        return null;
    }

    @Override
    public XMap headerMap() {
        return null;
    }

    @Override
    public Object response() {
        return null;
    }

    @Override
    public void charset(String charset) {

    }

    @Override
    protected void contentTypeDoSet(String contentType) {

    }

    @Override
    public void output(String str) {

    }

    @Override
    public void output(InputStream stream) {

    }

    @Override
    public OutputStream outputStream() throws IOException {
        return null;
    }

    @Override
    public void headerSet(String key, String val) {

    }

    @Override
    public void headerAdd(String key, String val) {

    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {

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
    public void status(int status) {

    }
}
