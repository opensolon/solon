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
package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.Utils;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.net.http.HttpCallback;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.AbstractHttpUtils;
import org.noear.solon.net.http.impl.HttpTimeout;
import org.noear.solon.net.http.impl.HttpUploadFile;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Http 工具 JDK HttpURLConnection 实现
 *
 * @author noear
 * @since 3.0
 */
public class JdkHttpUtilsImpl extends AbstractHttpUtils implements HttpUtils {

    public JdkHttpUtilsImpl(String url) {
        super(url);
        _timeout = new HttpTimeout(60);
    }

    @Override
    protected HttpResponse execDo(String method, HttpCallback callback) throws IOException {
        String url0 = urlRebuild(_url, _charset);
        method = method.toUpperCase();

        HttpURLConnection _builder = (HttpURLConnection) new URL(url0).openConnection();

        if (_timeout != null) {
            _builder.setConnectTimeout(_timeout.connectTimeout * 1000);
            _builder.setReadTimeout(_timeout.readTimeout * 1000);
        }

        String contentTypeDef = null;
        if (_headers != null) {
            contentTypeDef = _headers.get("Content-Type");
            for (KeyValues<String> kv : _headers) {
                for (String val : kv.getValues()) {
                    _builder.addRequestProperty(kv.getKey(), val);
                }
            }
        }

        if (_cookies != null) {
            _builder.setRequestProperty("Cookie", getRequestCookieString(_cookies));
        }

        _builder.setRequestMethod(method);

        _builder.setDoInput(true);
        if (!"GET".equals(method) || !"HEAD".equals(method)) {
            _builder.setUseCaches(false);
        }

        if (_bodyRaw != null) {
            String contentType = Utils.annoAlias(_bodyRaw.contentType, contentTypeDef);
            _builder.setRequestProperty("Content-Type", contentType);

            _builder.setDoOutput(true);
            try (OutputStream out = _builder.getOutputStream()) {
                IoUtil.transferTo(_bodyRaw.content, out);
                out.flush();
            }
        } else {
            if (_multipart) {
                _builder.setDoOutput(true);
                new FormDataBody(_charset).write(_builder, _files, _params);
            } else if (_params != null) {
                _builder.setDoOutput(true);
                new FormBody(_charset).write(_builder, _params);
            } else {
                //HEAD 可以为空
            }
        }

        //
        return new JdkHttpResponseImpl(_builder);
    }

    public static class FormDataBody {
        private static final String horizontalLine = "--------------------------";
        private static final String lineFeed = "\r\n";
        private static final String fileFormat = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\nContent-Type: %s";
        private static final String textFormat = "Content-Disposition: form-data; name=\"%s\"";

        final Charset charset;
        final String contentType;
        final String separator;
        final String endFlag;

        public FormDataBody(Charset charset) {
            long randomNumber = ThreadLocalRandom.current().nextLong();
            this.contentType = "multipart/form-data; boundary=" + horizontalLine + randomNumber;
            this.separator = "--" + horizontalLine + randomNumber;
            this.endFlag = lineFeed + separator + "--" + lineFeed;
            this.charset = charset;
        }

        void write(HttpURLConnection http, MultiMap<HttpUploadFile> fileMap, MultiMap<String> paramMap) throws IOException {
            http.setRequestProperty("Content-Type", contentType);

            try (OutputStream out = http.getOutputStream()) {
                if (fileMap != null) {
                    for (KeyValues<HttpUploadFile> kv : fileMap) {
                        for (HttpUploadFile val : kv.getValues()) {
                            appendFile(out, kv.getKey(), val);
                        }
                    }
                }

                if (paramMap != null) {
                    for (KeyValues<String> kv : paramMap) {
                        for (String val : kv.getValues()) {
                            appendText(out, kv.getKey(), val);
                        }
                    }
                }

                out.write(this.endFlag.getBytes(this.charset));
                out.flush();
            }
        }

        private void appendFile(OutputStream outputStream, String key, HttpUploadFile value) throws IOException {
            StringBuilder builder = new StringBuilder(1024);

            // append 头部信息
            builder.append(lineFeed).append(separator).append(lineFeed);
            builder.append(String.format(fileFormat, key, value.fileName, value.fileStream.contentType)).append(lineFeed);
            builder.append(lineFeed);
            outputStream.write(builder.toString().getBytes(charset));
            // append 实体
            IoUtil.transferTo(value.fileStream.content, outputStream);
            // append 换行
            //outputStream.write(lineFeed.getBytes(this.charset));
            outputStream.flush();
        }

        private void appendText(OutputStream outputStream, String key, String value) throws IOException {
            StringBuilder builder = new StringBuilder(1024);

            // append 头部信息
            builder.append(lineFeed).append(separator).append(lineFeed);
            builder.append(String.format(textFormat, key)).append(lineFeed);
            builder.append(lineFeed);
            // append 实体
            builder.append(value);
            outputStream.write(builder.toString().getBytes(this.charset));
            // append 换行
            //outputStream.write(lineFeed.getBytes(this.charset));
            outputStream.flush();
        }
    }

    public static class FormBody {
        final String contentType;
        final Charset charset;

        FormBody(Charset charset) {
            this.charset = charset;
            this.contentType = "application/x-www-form-urlencoded";
            //this.contentType = "application/x-www-form-urlencoded; charset=" + this.charset.name();
        }

        void write(HttpURLConnection http, MultiMap<String> paramMap) throws IOException {
            http.setRequestProperty("Content-Type", contentType);

            try (OutputStream out = http.getOutputStream()) {
                StringBuilder builder = new StringBuilder(128);
                paramMap.forEach(kv -> {
                    // urlEncode : if charset is empty not do Encode
                    for (Object val : kv.getValues()) {
                        builder.append(urlEncode(kv.getKey(), charset));
                        builder.append("=");
                        builder.append(urlEncode(String.valueOf(val), charset));
                        builder.append("&");
                    }
                });

                String data = builder.delete(builder.length() - 1, builder.length()).toString();

                out.write(data.getBytes(charset));
            }
        }
    }

    public static String urlRebuild(String url, Charset charset) throws UnsupportedEncodingException {
        int pathOf = url.indexOf("://");
        int queryOf = url.indexOf("?");

        String schema = url.substring(0, pathOf + 3);
        String hostAndPath = queryOf > 0 ? url.substring(pathOf + 3, queryOf) : url.substring(pathOf + 3);
        String query = queryOf > 0 ? url.substring(queryOf) : "";

        if (hostAndPath.length() > 0) {
            String hostAndPath0 = URLDecoder.decode(hostAndPath, charset.name());

            if (hostAndPath.equals(hostAndPath0)) {
                hostAndPath = urlEncode(hostAndPath, charset);
                hostAndPath = hostAndPath.replace("%2F", "/").replace("%3A", ":");
            }
        }

        if (query.length() > 0) {
            String query0 = URLDecoder.decode(query, charset.name());
            if (query.equals(query0)) {
                query = urlEncode(query, charset);
                query = query.replace("%3F", "?").replace("%2F", "/").replace("%3A", ":").replace("%3D", "=").replace("%26", "&").replace("%23", "#");
            }
        }

        return schema + hostAndPath + query;
    }

    /**
     * url 编码
     */
    private static String urlEncode(String text, Charset charset) {
        if (Utils.isNotEmpty(text)) {
            try {
                return URLEncoder.encode(text, charset.name());
            } catch (UnsupportedEncodingException e) {
                // do non thing
            }
        }
        return text;
    }
}
