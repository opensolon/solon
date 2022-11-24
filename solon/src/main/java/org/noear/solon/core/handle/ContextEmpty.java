package org.noear.solon.core.handle;

import org.noear.solon.core.NvMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

/**
 * 通用上下文，空对象
 *
 * @author noear
 * @since 1.0
 * */
public class ContextEmpty extends Context {
    public static Context create(){
        return new ContextEmpty();
    }

    public ContextEmpty(){
        sessionState = new SessionStateEmpty();
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
    public String queryString() {
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
        return paramMap().get(key);
    }

    @Override
    public String param(String key, String def) {
        return paramMap().getOrDefault(key, def);
    }

    private NvMap paramMap = null;
    @Override
    public NvMap paramMap() {
        if(paramMap == null){
            paramMap = new NvMap();
        }
        return paramMap;
    }

    @Override
    public Map<String, List<String>> paramsMap() {
        return null;
    }

    @Override
    public List<UploadedFile> files(String key) throws Exception {
        return null;
    }

    @Override
    public String cookie(String key) {
        return cookieMap().get(key);
    }

    @Override
    public String cookie(String key, String def) {
        return cookieMap().getOrDefault(key,def);
    }

    NvMap cookieMap = null;
    @Override
    public NvMap cookieMap() {
        if(cookieMap == null){
            cookieMap = new NvMap();
        }
        return cookieMap;
    }

    private NvMap headerMap = null;
    @Override
    public NvMap headerMap() {
        if(headerMap == null){
            headerMap = new NvMap();
        }
        return headerMap;
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
    public OutputStream outputStream() {
        return null;
    }

    @Override
    public void headerSet(String key, String val) {
        headerMap().put(key,val);
    }

    @Override
    public void headerAdd(String key, String val) {
        headerMap().put(key,val);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        cookieMap().put(key,val);
    }

    @Override
    public void redirect(String url, int code) {

    }

    private int status = 200;
    @Override
    public int status() {
        return status;
    }

    @Override
    protected void statusDoSet(int status) {
        this.status = status;
    }

    @Override
    public void flush() throws IOException{

    }
}
