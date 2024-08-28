package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.http.HttpPartFile;
import org.noear.solon.boot.io.LimitedInputException;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.boot.web.uploadfile.HttpMultipart;
import org.noear.solon.boot.web.uploadfile.HttpMultipartCollection;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;

import java.io.IOException;

/**
 * 主体工具类
 *
 * @author noear
 * @since 2.9
 */
public class BodyUtils {
    /**
     * 解码多部分主体
     */
    public static void decodeMultipart(Context ctx, MultiMap<UploadedFile> filesMap) {
        try {
            HttpMultipartCollection parts = new HttpMultipartCollection(ctx.contentType(), ctx.bodyAsStream());

            while (parts.hasNext()) {
                HttpMultipart part = parts.next();
                String name = ServerProps.urlDecode(part.getName());

                if (isFile(part)) {
                    doBuildFiles(name, filesMap, part);
                } else {
                    ctx.paramAdd(name, part.getString());
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
                ctx.paramAdd(name, value);
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