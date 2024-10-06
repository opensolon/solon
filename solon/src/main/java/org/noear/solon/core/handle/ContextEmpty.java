/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.handle;

import org.noear.solon.core.util.MultiMap;
import org.noear.solon.lang.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * 通用上下文，空对象
 *
 * @author noear
 * @since 1.0
 * */
public class ContextEmpty extends Context {
    public static Context create() {
        return new ContextEmpty();
    }

    public ContextEmpty() {
        sessionState = new SessionStateEmpty();
    }

    private Object request = null;

    @Override
    public Object request() {
        return request;
    }

    public ContextEmpty request(Object request) {
        this.request = request;
        return this;
    }


    @Override
    public String remoteIp() {
        return null;
    }

    @Override
    public int remotePort() {
        return 0;
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
    public boolean isSecure() {
        return false;
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public long contentLength() {
        return 0L;
    }

    @Override
    public String contentType() {
        return headerMap().get("Content-Type");
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

    protected MultiMap<String> paramMap = null;

    @Override
    public MultiMap<String> paramMap() {
        if (paramMap == null) {
            paramMap = new MultiMap<>();
        }
        return paramMap;
    }


    protected MultiMap<UploadedFile> filesMap = null;

    @Override
    public MultiMap<UploadedFile> fileMap() {
        if (filesMap == null) {
            filesMap = new MultiMap<>();
        }

        return filesMap;
    }

    @Override
    public void filesDelete() throws IOException {

    }


    protected MultiMap<String> cookieMap = null;

    @Override
    public MultiMap<String> cookieMap() {
        if (cookieMap == null) {
            cookieMap = new MultiMap<>();
        }
        return cookieMap;
    }

    protected MultiMap<String> headerMap = null;

    @Override
    public MultiMap<String> headerMap() {
        if (headerMap == null) {
            headerMap = new MultiMap<>();
        }
        return headerMap;
    }

    @Override
    public String sessionId() {
        return null;
    }

    @Override
    public <T> T session(String name, Class<T> clz) {
        return null;
    }

    @Override
    public <T> T sessionOrDefault(String name, @NonNull T def) {
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
    public void sessionReset() {

    }

    private Object response = null;

    @Override
    public Object response() {
        return response;
    }

    public ContextEmpty response(Object response) {
        this.response = response;
        return this;
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
    public GZIPOutputStream outputStreamAsGzip() throws IOException {
        return null;
    }

    @Override
    public void outputAsFile(DownloadedFile file) throws IOException {

    }

    @Override
    public void outputAsFile(File file) throws IOException {

    }

    @Override
    public void headerSet(String key, String val) {
        headerMap().put(key, val);
    }

    @Override
    public void headerAdd(String key, String val) {
        headerMap().add(key, val);
    }

    @Override
    public String headerOfResponse(String name) {
        return headerMap().get(name);
    }

    @Override
    public Collection<String> headerValuesOfResponse(String name) {
        return Arrays.asList(headerMap().get(name));
    }

    @Override
    public Collection<String> headerNamesOfResponse() {
        return headerMap().keySet();
    }

    @Override
    public void cookieSet(Cookie cookie) {

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
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean asyncSupported() {
        return false;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener, Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void asyncComplete() {
        throw new UnsupportedOperationException();
    }
}