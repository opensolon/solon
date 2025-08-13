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
package org.noear.solon.server.handle;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerProps;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.lang.NonNull;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.server.util.OutputUtils;

import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Web 上下文基类
 *
 * @author noear
 * @since 1.11
 * @since 2.3
 * @since 3.5
 */
public abstract class ContextBase extends Context {
    private String path;

    /**
     * 获取请求的URI路径
     */
    public String path() {
        if (path == null && url() != null) {
            if (ServerProps.request_useRawpath) {
                path = uri().getRawPath();
            } else {
                path = uri().getPath();
            }

            if (path == null) {
                this.path = "";
            }

            if (path.contains("//")) {
                path = Utils.trimDuplicates(path, '/');
            }
        }

        return path;
    }

    /**
     * 内存类型
     */
    @Override
    public String contentType() {
        return header(HeaderNames.HEADER_CONTENT_TYPE);
    }


    private String contentCharset;

    /**
     * 内存字符集
     */
    @Override
    public String contentCharset() {
        if (contentCharset == null) {
            contentCharset = DecodeUtils.extractQuotedValueFromHeader(contentType(), "charset");

            if (Utils.isEmpty(contentCharset)) {
                contentCharset = ServerProps.request_encoding;
            }

            if (Utils.isEmpty(contentCharset)) {
                contentCharset = Solon.encoding();
            }
        }

        return contentCharset;
    }

    @Override
    public GZIPOutputStream outputStreamAsGzip() throws IOException {
        headerSet("Vary", "Accept-Encoding");
        headerSet("Content-Encoding", "gzip");
        return new GZIPOutputStream(outputStream(), 4096, true);
    }

    /**
     * 输出为文件
     */
    @Override
    public void outputAsFile(DownloadedFile file) throws IOException {
        OutputUtils.global().outputFile(this, file, file.isAttachment());
    }

    /**
     * 输出为文件
     */
    @Override
    public void outputAsFile(File file) throws IOException {
        OutputUtils.global().outputFile(this, file, true);
    }


    /**
     * 获取 sessionId
     */
    @Override
    public final String sessionId() {
        return sessionState().sessionId();
    }

    /**
     * 获取 session 状态
     *
     * @param name 状态名
     */
    @Override
    public final <T> T session(String name, Class<T> clz) {
        return sessionState().sessionGet(name, clz);
    }

    /**
     * 获取 session 状态（泛型转换，存在风险）
     *
     * @param name 状态名
     */
    @Override
    public final <T> T sessionOrDefault(String name, @NonNull T def) {
        if (def == null) {
            return (T) session(name, Object.class);
        }

        Object tmp = session(name, def.getClass());
        if (tmp == null) {
            return def;
        } else {
            return (T) tmp;
        }
    }

    /**
     * 获取 session 状态，并以 int 型输出
     *
     * @param name 状态名
     * @since 1.6
     */
    @Override
    public final int sessionAsInt(String name) {
        return sessionAsInt(name, 0);
    }

    /**
     * 获取 session 状态，并以 int 型输出
     * output
     *
     * @param name 状态名
     * @since 1.6
     */
    @Override
    public final int sessionAsInt(String name, int def) {
        Object tmp = session(name, Object.class);
        if (tmp == null) {
            return def;
        } else {
            if (tmp instanceof Number) {
                return ((Number) tmp).intValue();
            } else if (tmp instanceof String) {
                String str = (String) tmp;
                if (str.length() > 0) {
                    return Integer.parseInt(str);
                }
            }

            return def;
        }
    }

    /**
     * 获取 session 状态，并以 long 型输出
     *
     * @param name 状态名
     * @since 1.6
     */
    @Override
    public final long sessionAsLong(String name) {
        return sessionAsLong(name, 0L);
    }

    /**
     * 获取 session 状态，并以 long 型输出
     *
     * @param name 状态名
     * @since 1.6
     */
    @Override
    public final long sessionAsLong(String name, long def) {
        Object tmp = session(name, Object.class);
        if (tmp == null) {
            return def;
        } else {
            if (tmp instanceof Number) {
                return ((Number) tmp).longValue();
            } else if (tmp instanceof String) {
                String str = (String) tmp;
                if (str.length() > 0) {
                    return Long.parseLong(str);
                }
            }

            return def;
        }
    }

    /**
     * 获取 session 状态，并以 double 型输出
     *
     * @param name 状态名
     * @since 1.6
     */
    @Override
    public final double sessionAsDouble(String name) {
        return sessionAsDouble(name, 0.0D);
    }

    /**
     * 获取 session 状态，并以 double 型输出
     *
     * @param name 状态名
     * @since 1.6
     */
    @Override
    public final double sessionAsDouble(String name, double def) {
        Object tmp = session(name, Object.class);
        if (tmp == null) {
            return def;
        } else {
            if (tmp instanceof Number) {
                return ((Number) tmp).doubleValue();
            } else if (tmp instanceof String) {
                String str = (String) tmp;
                if (str.length() > 0) {
                    return Double.parseDouble(str);
                }
            }

            return def;
        }
    }

    /**
     * 设置 session 状态
     *
     * @param name 状态名
     * @param val  值
     */
    @Override
    public final void sessionSet(String name, Object val) {
        sessionState().sessionSet(name, val);
    }

    /**
     * 移除 session 状态
     *
     * @param name 状态名
     */
    @Override
    public final void sessionRemove(String name) {
        sessionState().sessionRemove(name);
    }

    /**
     * 清空 session 状态
     */
    @Override
    public final void sessionClear() {
        sessionState().sessionClear();
    }

    @Override
    public void sessionReset() {
        sessionState().sessionReset();
    }

    protected final MultiMap<UploadedFile> _fileMap = new MultiMap<>();

    /**
     * 删除所有临时文件
     */
    @Override
    public void filesDelete() throws IOException {
        //批量删除临时文件
        for (KeyValues<UploadedFile> kv : _fileMap) {
            for (UploadedFile file : kv.getValues()) {
                file.delete();
            }
        }
    }

    //一些特殊的boot才有效
    protected void innerCommit() throws IOException {
    }
}