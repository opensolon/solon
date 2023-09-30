package org.noear.solon.core.util;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 输入输出工具
 *
 * @author noear
 * @since 2.4
 */
public class IoUtil {
    public static String transferToString(InputStream ins) throws IOException {
        return transferToString(ins, Solon.encoding());
    }

    /**
     * 将输入流转换为字符串
     *
     * @param ins     输入流
     * @param charset 字符集
     */
    public static String transferToString(InputStream ins, String charset) throws IOException {
        if (ins == null) {
            return null;
        }

        ByteArrayOutputStream outs = transferTo(ins, new ByteArrayOutputStream());

        if (Utils.isEmpty(charset)) {
            return outs.toString();
        } else {
            return outs.toString(charset);
        }
    }

    /**
     * 将输入流转换为byte数组
     *
     * @param ins 输入流
     */
    public static byte[] transferToBytes(InputStream ins) throws IOException {
        if (ins == null) {
            return null;
        }

        return transferTo(ins, new ByteArrayOutputStream()).toByteArray();
    }

    /**
     * 将输入流转换为输出流
     *
     * @param ins 输入流
     * @param out 输出流
     */
    public static <T extends OutputStream> T transferTo(InputStream ins, T out) throws IOException {
        if (ins == null || out == null) {
            return null;
        }

        int len = 0;
        byte[] buf = new byte[512];
        while ((len = ins.read(buf)) != -1) {
            out.write(buf, 0, len);
        }

        return out;
    }

    /**
     * 将输入流转换为输出流
     *
     * @param ins    输入流
     * @param out    输出流
     * @param start  开始位
     * @param length 长度
     */
    public static <T extends OutputStream> T transferTo(InputStream ins, T out, long start, long length) throws IOException {
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
