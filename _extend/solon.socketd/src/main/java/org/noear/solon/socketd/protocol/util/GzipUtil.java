package org.noear.solon.socketd.protocol.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class GzipUtil {
    public static ByteArrayOutputStream compressDo(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(bytes);
        }

        return out;
    }

    public static byte[] compress(byte[] bytes) throws IOException {

        return compressDo(bytes).toByteArray();
    }


    public static ByteArrayOutputStream uncompressDo(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        GZIPInputStream ungzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = ungzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }

        return out;
    }

    public static byte[] uncompress(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }

        ByteArrayOutputStream tmp = uncompressDo(bytes);
        if (tmp == null) {
            return null;
        } else {
            return tmp.toByteArray();
        }
    }
}
