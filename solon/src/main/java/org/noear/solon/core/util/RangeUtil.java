package org.noear.solon.core.util;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;

import java.io.*;
import java.net.URLEncoder;

/**
 * 分片输出工具类
 *
 * @author noear
 * @since 2.4
 */
public class RangeUtil {
    private static RangeUtil global = new RangeUtil();
    public static RangeUtil global() {
        return global;
    }
    public static void globalSet(RangeUtil instance) {
        if (instance != null) {
            global = instance;
        }
    }

    //////////////


    /**
     * 输出文件
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
            RangeUtil.global().outputStream(ctx, ins, file.getContentSize());
        }
    }

    /**
     * 输出文件
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
            RangeUtil.global().outputStream(ctx, ins, file.length());
        }
    }


    /**
     * 输出流
     */
    public void outputStream(Context ctx, InputStream stream, long streamSize) throws IOException {
        ctx.headerSet("Accept-Ranges", "bytes");

        if ("HEAD".equals(ctx.method())) {
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
            transferTo(stream, ctx.outputStream(), start, size);
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

    /**
     * 转换流
     */
    protected <T extends OutputStream> T transferTo(InputStream ins, T out, long start, long length) throws IOException {
        int len = 0;
        byte[] buf = new byte[512];
        int bufMax = buf.length;
        if (length < bufMax) {
            bufMax = (int) length;
        }

        if (start > 0) {
            ins.skip(start);
        }

        while ((len = ins.read(buf, 0, bufMax)) != -1) {
            out.write(buf, 0, len);

            length -= len;
            if (bufMax > length) {
                bufMax = (int) length;

                if (bufMax == 0) {
                    break;
                }
            }
        }

        return out;
    }
}