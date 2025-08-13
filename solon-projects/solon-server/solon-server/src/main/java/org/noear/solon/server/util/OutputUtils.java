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

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.server.prop.GzipProps;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.util.DateUtil;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.RunUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.zip.GZIPOutputStream;

/**
 * Web 文件输出工具类
 *
 * @author noear
 * @since 2.4
 */
public class OutputUtils {
    static final Logger log = LoggerFactory.getLogger(OutputUtils.class);

    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String LAST_MODIFIED = "Last-Modified";

    private static OutputUtils global = new OutputUtils();

    public static OutputUtils global() {
        return global;
    }

    public static void globalSet(OutputUtils instance) {
        if (instance != null) {
            global = instance;
        }
    }

    //////////////


    /**
     * 输出文件（主要是给动态输出用）
     */
    public void outputFile(Context ctx, DownloadedFile file, boolean asAttachment) throws IOException {
        //type
        if (Utils.isNotEmpty(file.getContentType())) {
            ctx.contentType(file.getContentType());
        }

        //etag
        if (Utils.isNotEmpty(file.getETag())) {
            ctx.headerSet("ETag", file.getETag());
        }

        //cache
        if (file.getMaxAgeSeconds() > 0) {
            String modified_since = ctx.header("If-Modified-Since");
            String modified_now = DateUtil.toGmtString(file.getLastModified());

            if (modified_since != null) {
                if (modified_since.equals(modified_now)) {
                    RunUtil.runAndTry(file::close);
                    ctx.headerSet(CACHE_CONTROL, "max-age=" + file.getMaxAgeSeconds());//单位秒
                    ctx.headerSet(LAST_MODIFIED, modified_now);
                    ctx.status(304);
                    return;
                }
            }

            ctx.headerSet(CACHE_CONTROL, "max-age=" + file.getMaxAgeSeconds());//单位秒
            ctx.headerSet(LAST_MODIFIED, modified_now);
        }

        //attachment
        if (Utils.isNotEmpty(file.getName())) {
            String fileName = URLEncoder.encode(file.getName(), Solon.encoding());
            if (asAttachment) {
                ctx.headerSet("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            } else {
                ctx.headerSet("Content-Disposition", "filename=\"" + fileName + "\"");
            }
        }

        //output
        try (InputStream ins = file.getContent()) {
            outputStream(ctx, ins, file.getContentSize(), file.getContentType());
        }
    }

    /**
     * 输出文件（主要是给动态输出用）
     */
    public void outputFile(Context ctx, File file, boolean asAttachment) throws IOException {
        //输出文件名
        if (Utils.isNotEmpty(file.getName())) {
            String fileName = URLEncoder.encode(file.getName(), Solon.encoding());
            if (asAttachment) {
                ctx.headerSet("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            } else {
                ctx.headerSet("Content-Disposition", "filename=\"" + fileName + "\"");
            }
        }

        //输出内容类型
        String contentType = Utils.mime(file.getName());
        if (Utils.isNotEmpty(contentType)) {
            ctx.contentType(contentType);
        }

        try (InputStream ins = new FileInputStream(file)) {
            outputStream(ctx, ins, file.length(), contentType);
        }
    }

    /**
     * 输出文件（主要是给静态文件用）
     */
    public void outputFile(Context ctx, URL file, String conentType, boolean useCaches) throws IOException {
        //
        // todo: 有 gzip 需求时，可以再增加 demo.js 由 demo.js.gz 输出的尝试（如果有）
        //
        if (useCaches) {
            //使用 uri 缓存（jdk 内部有缓存）
            try (InputStream stream = file.openStream()) {
                ctx.contentType(conentType);
                outputStream(ctx, stream, stream.available(), conentType);
            }
        } else {
            //说明不需要 uri 缓存; 或者是调试模式
            URLConnection connection = file.openConnection();
            connection.setUseCaches(false);

            try (InputStream stream = connection.getInputStream()) {
                ctx.contentType(conentType);
                outputStream(ctx, stream, stream.available(), conentType);
            }
        }
    }


    /**
     * 输出流
     */
    public void outputStream(Context ctx, InputStream stream, long streamSize, String mime) throws IOException {
        if (GzipProps.requiredGzip(ctx, mime, streamSize)) {
            outputStreamAsGzip(ctx, stream);
        } else {
            outputStreamAsRange(ctx, stream, streamSize);
        }
    }

    /**
     * 输出流，做为 gzip 输出
     */
    public void outputStreamAsGzip(Context ctx, InputStream stream) throws IOException {
        //支持 gzip
        GZIPOutputStream gzipOut = ctx.outputStreamAsGzip();
        IoUtil.transferTo(stream, gzipOut);
    }

    /**
     * 输出流，做为 range 形式输出（如果支持）
     */
    public void outputStreamAsRange(Context ctx, InputStream stream, long streamSize) throws IOException {
        if (streamSize > 0) {
            //支持分版
            ctx.headerSet("Accept-Ranges", "bytes");
        } else {
            //大小未知时，不支持分片
            ctx.status(200);
            ctx.output(stream);
            return;
        }

        if ("HEAD".equals(ctx.method())) {
            //如果客户端在探测
            ctx.contentLength(streamSize);
            ctx.status(200);
            return;
        }


        String range = ctx.header("Range");
        long start = 0, end = 0;
        long size = 0;

        if (Utils.isEmpty(range)) {
            ctx.contentLength(streamSize);
            ctx.status(200);
            ctx.output(stream);
            return;
        } else {
            String[] ss1 = range.split("=");

            if (ss1.length == 2) {
                String unit = ss1[0];
                String[] ss2 = ss1[1].split("-");

                if ("bytes".equals(unit)) {
                    if (ss2.length == 2) {
                        start = getLong(ss2[0]);
                        end = getLong(ss2[1]);
                    } else if (ss2.length == 1) {
                        start = getLong(ss2[0]);
                        end = streamSize - 1;
                    } else {
                        //说明格式有误
                        ctx.status(416);
                        return;
                    }
                } else {
                    //说明格式有误
                    ctx.status(416);
                    return;
                }
            } else {
                //说明格式有误
                ctx.status(416);
                return;
            }
        }

        if (end > 0) {
            size = end - start + 1; //从0开始的
        } else {
            end = (streamSize - 1);
            size = end - start + 1;//从0开始的
        }

        if (end < 1 || size < 0) {
            //说明格式有误
            ctx.status(416);
            return;
        }

        if (size > (streamSize - start)) {
            //说明大小超界了
            ctx.status(416);
            return;
        }

        ctx.contentLength(size);
        ctx.status(206);

        ctx.headerSet("Connection", "keep-alive");
        ctx.headerSet("Content-Range", "bytes " + start + "-" + end + "/" + streamSize);

        //ctx.output(stream);
        try {
            IoUtil.transferTo(stream, ctx.outputStream(), start, size);
        } catch (IOException e) {
            //会很常见，没必要异常
            log.debug("The http range output is abnormal: " + e.getMessage());
        }
    }

    /**
     * 获取长整型值
     */
    protected long getLong(String str) {
        if (Utils.isEmpty(str)) {
            return 0;
        } else {
            return Long.parseLong(str);
        }
    }
}