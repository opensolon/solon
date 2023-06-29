package org.noear.solon.boot.vertx;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2023/6/24 created
 */
public class VxHttpContext extends Context {
    public VxHttpContext(HttpServerRequest request, HttpServerResponse response){

    }

    @Override
    public Object request() {
        return null;
    }

    @Override
    public String ip() {
        return null;
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
    public String contentCharset() {
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
    public NvMap paramMap() {
        return null;
    }

    @Override
    public Map<String, List<String>> paramsMap() {
        return null;
    }

    @Override
    public Map<String, List<UploadedFile>> filesMap() throws IOException {
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
    public Map<String, List<String>> headersMap() {
        return null;
    }

    @Override
    public String sessionId() {
        return null;
    }

    @Override
    public Object session(String name) {
        return null;
    }

    @Override
    public <T> T sessionOrDefault(String name, T def) {
        return null;
    }

    @Override
    public int sessionAsInt(String name) {
        return 0;
    }

    @Override
    public int sessionAsInt(String name, int def) {
        return 0;
    }

    @Override
    public long sessionAsLong(String name) {
        return 0;
    }

    @Override
    public long sessionAsLong(String name, long def) {
        return 0;
    }

    @Override
    public double sessionAsDouble(String name) {
        return 0;
    }

    @Override
    public double sessionAsDouble(String name, double def) {
        return 0;
    }

    @Override
    public void sessionSet(String name, Object val) {

    }

    @Override
    public void sessionRemove(String name) {

    }

    @Override
    public void sessionClear() {

    }

    @Override
    public Object response() {
        return null;
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

    @Override
    public boolean asyncSupported() {
        return false;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener) {

    }

    @Override
    public void asyncComplete() throws IOException {

    }
}
