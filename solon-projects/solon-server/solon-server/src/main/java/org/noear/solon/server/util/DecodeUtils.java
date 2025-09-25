/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.solon.server.util;

import org.noear.solon.Utils;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.http.HttpPartFile;
import org.noear.solon.server.io.LimitedInputException;
import org.noear.solon.server.io.LimitedInputStream;
import org.noear.solon.server.http.uploadfile.HttpMultipart;
import org.noear.solon.server.http.uploadfile.HttpMultipartCollection;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * Web 解码工具类
 *
 * @author noear
 * @since 2.9
 * @since 3.5
 */
public class DecodeUtils {
    /**
     * 解码多部分主体
     */
    public static void decodeMultipart(Context ctx, InputStream unlimitedInputStream, MultiMap<UploadedFile> filesMap) {
        try {
            LimitedInputStream limitedInputStream = new LimitedInputStream(unlimitedInputStream, ServerProps.request_maxFileSize);
            HttpMultipartCollection parts = new HttpMultipartCollection(ctx.contentType(), limitedInputStream);

            while (parts.hasNext()) {
                HttpMultipart part = parts.next();
                String name = ServerProps.urlDecode(part.getName());

                if (isFile(part)) {
                    doBuildFiles(name, filesMap, part);
                } else {
                    ctx.paramMap().add(name, part.getString());
                }
            }
        } catch (Exception e) {
            throw status4xx(ctx, e);
        }
    }

    private static void doBuildFiles(String name, MultiMap<UploadedFile> filesMap, HttpMultipart part) throws IOException {
        KeyValues<UploadedFile> list = filesMap.holder(name);

        String contentType = part.getHeaders().get("Content-Type");
        String filename = part.getFilename();
        String extension = null;
        int idx = filename.lastIndexOf(".");
        if (idx > 0) {
            extension = filename.substring(idx + 1);
        }

        HttpPartFile partFile = new HttpPartFile(filename, new LimitedInputStream(part.getBody(), ServerProps.request_maxFileSize));
        UploadedFile f1 = new UploadedFile(partFile::delete, contentType, partFile.getSize(), partFile.getContent(), filename, extension);

        list.addValue(f1);
    }

    private static boolean isField(HttpMultipart filePart) {
        return filePart.getFilename() == null;
    }

    private static boolean isFile(HttpMultipart filePart) {
        return !isField(filePart);
    }

    //////////////////////

    /**
     * 解码 FormUrlencoded
     */
    public static void decodeFormUrlencoded(Context ctx) throws IOException {
        decodeFormUrlencoded(ctx, true);
    }

    /**
     * 解码 FormUrlencoded
     *
     * @param excludePost 排除 post
     */
    public static void decodeFormUrlencoded(Context ctx, boolean excludePost) throws IOException {
        if (excludePost) {
            if (MethodType.POST.name.equals(ctx.method())) {
                // post 除外
                return;
            }
        }

        if (ctx.isFormUrlencoded() == false) {
            return;
        }

        if (Utils.isEmpty(ctx.bodyNew())) {
            return;
        }

        String[] ss = ctx.bodyNew().split("&");

        for (String s1 : ss) {
            int idx = s1.indexOf('=');
            if (idx > 0) {
                String name = ServerProps.urlDecode(s1.substring(0, idx));
                String value = ServerProps.urlDecode(s1.substring(idx + 1));
                ctx.paramMap().add(name, value);
            }
        }
    }

    public static void decodeCookies(Context ctx, String cookies) {
        if (Utils.isEmpty(cookies)) {
            return;
        }

        decodeCookiesDo(ctx, cookies, 0);
    }

    private static  void decodeCookiesDo(Context ctx, String cookies, int offset) {
        //去掉头部空隔
        while (offset < cookies.length() && cookies.charAt(offset) == ' ') {
            offset++;
        }

        int idx1 = cookies.indexOf("=", offset);
        if (idx1 < 0) {
            return;
        }
        String name = cookies.substring(offset, idx1);

        int idx2 = cookies.indexOf(";", idx1);
        if (idx2 < 0) {
            //如果没有找到，则后面全是
            idx2 = cookies.length();
        }

        //去掉尾部空隔
        offset = idx2;
        while (offset > idx1 && cookies.charAt(offset - 1) == ' ') {
            offset--;
        }

        String value = cookies.substring(idx1 + 1, offset);

        ctx.cookieMap().add(name, value.trim());

        if (idx2 < 0) {
            return;
        }

        decodeCookiesDo(ctx, cookies, idx2 + 1);
    }

    /**
     * 提取内容长度
     * */
    public static long decodeContentLengthLong(Context ctx) {
        String contentLength = ctx.header("Content-Length");
        return contentLength != null && !contentLength.isEmpty() ? Long.parseLong(contentLength) : -1L;
    }

    /**
     * 提取头信息中的分段值（例：Content-Type:text/json;charset=utf-8）
     * */
    public static String extractQuotedValueFromHeader(String header, String key) {
        if(Utils.isEmpty(header)){
            return null;
        }

        int keypos = 0;
        int pos = -1;
        boolean whiteSpace = true;
        boolean inQuotes = false;

        int i;
        int start;
        for(i = 0; i < header.length() - 1; ++i) {
            start = header.charAt(i);
            if (inQuotes) {
                if (start == 34) {
                    inQuotes = false;
                }
            } else {
                if (key.charAt(keypos) == start && (whiteSpace || keypos > 0)) {
                    ++keypos;
                    whiteSpace = false;
                } else if (start == 34) {
                    keypos = 0;
                    inQuotes = true;
                    whiteSpace = false;
                } else {
                    keypos = 0;
                    whiteSpace = start == 32 || start == 59 || start == 9;
                }

                if (keypos == key.length()) {
                    if (header.charAt(i + 1) == '=') {
                        pos = i + 2;
                        break;
                    }

                    keypos = 0;
                }
            }
        }

        if (pos == -1) {
            return null;
        } else {
            char c;
            if (header.charAt(pos) == '"') {
                start = pos + 1;

                for(i = start; i < header.length(); ++i) {
                    c = header.charAt(i);
                    if (c == '"') {
                        break;
                    }
                }

                return header.substring(start, i);
            } else {
                for(i = pos; i < header.length(); ++i) {
                    c = header.charAt(i);
                    if (c == ' ' || c == '\t' || c == ';') {
                        break;
                    }
                }

                return header.substring(pos, i);
            }
        }
    }

    //////////////////////


    /**
     * 清洗 uri
     * */
    public static String rinseUri(String uri) {
        int idx = uri.indexOf("://");

        if (idx < 0) {
            idx = uri.indexOf("//");
        } else {
            idx = uri.indexOf("//", idx + 4);
        }

        if (idx < 0) {
            return uri;
        } else {
            if (idx > 0) {
                String head = uri.substring(0, idx);
                String content = uri.substring(idx);

                content = Utils.trimDuplicates(content, '/');

                return head + content;
            } else {
                return Utils.trimDuplicates(uri, '/');
            }
        }
    }


    //////////////////////

    /**
     * 构建关状态异常
     */
    public static StatusException status4xx(Context ctx, Exception e) {
        if (e instanceof StatusException) {
            return (StatusException) e;
        } else {
            if (isBodyLargerEx(e)) {
                return new StatusException("Request Entity Too Large: " + ctx.method() + " " + ctx.pathNew(), e, 413);
            } else {
                return new StatusException("Bad Request:" + ctx.method() + " " + ctx.pathNew(), e, 400);
            }
        }
    }

    /**
     * 是否为 body larger ex?
     */
    public static boolean isBodyLargerEx(Throwable e) {
        return hasLargerStr(e) || hasLargerStr(e.getCause());
    }

    private static boolean hasLargerStr(Throwable e) {
        if (e instanceof LimitedInputException) {
            return true;
        }

        return false;
    }
}