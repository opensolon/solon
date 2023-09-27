package org.noear.solon.boot.web;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.prop.GzipProps;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.LogUtil;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.zip.GZIPOutputStream;

/**
 * 文件输出工具类
 *
 * @author noear
 * @since 2.4
 */
public class OutputUtils {
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
        if (Utils.isNotEmpty(file.getName())) {
            String fileName = URLEncoder.encode(file.getName(), Solon.encoding());
            if (asAttachment) {
                ctx.headerSet("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            } else {
                ctx.headerSet("Content-Disposition", "filename=\"" + fileName + "\"");
            }
        }

        //输出内容类型
        if (Utils.isNotEmpty(file.getContentType())) {
            ctx.contentType(file.getContentType());
        }

        try (InputStream ins = file.getContent()) {
            OutputUtils.global().outputStream(ctx, ins, file.getContentSize(), file.getContentType());
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
            OutputUtils.global().outputStream(ctx, ins, file.length(), contentType);
        }
    }

    /**
     * 输出文件（主要是给静态文件用）
     */
    public void outputFile(Context ctx, URL file , String conentType,boolean useCaches) throws IOException {
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
        ctx.status(200);
        ctx.headerSet("Vary", "Accept-Encoding");
        ctx.headerSet("Content-Encoding", "gzip");
        GZIPOutputStream gzipOut = new GZIPOutputStream(ctx.outputStream(), 4096, true);
        IoUtil.transferTo(stream, gzipOut);
        gzipOut.flush();
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
            LogUtil.global().warn("The http range output is abnormal: " + e.getMessage());
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