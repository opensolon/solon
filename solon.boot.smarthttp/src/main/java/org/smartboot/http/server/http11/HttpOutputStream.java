/*
 * Copyright (c) 2018, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: HttpOutputStream.java
 * Date: 2018-02-17
 * Author: sandao
 */

package org.smartboot.http.server.http11;

import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.utils.CharsetUtil;
import org.smartboot.http.utils.Consts;
import org.smartboot.http.utils.HeaderNameEnum;
import org.smartboot.http.utils.HttpHeaderConstant;
import org.smartboot.socket.util.QuickTimerTask;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/3
 */
final class HttpOutputStream extends OutputStream {
    private static final Map<String, byte[]>[] CONTENT_TYPE_CACHE = new Map[512];
    private static final Map<String, byte[]> CHUNKED_CACHE = new HashMap<>();
    private static final byte[] CHUNKED_END_BYTES = "0\r\n\r\n".getBytes(CharsetUtil.US_ASCII);
    private static SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    private static Date currentDate = new Date();
    private static byte[] date;

    static {
        for (int i = 0; i < CONTENT_TYPE_CACHE.length; i++) {
            CONTENT_TYPE_CACHE[i] = new HashMap<>();
        }
        flushDate();
        new ResponseDateTimer();
    }

    private DefaultHttpResponse response;
    private OutputStream outputStream;
    private boolean committed = false, closed = false;
    private boolean chunked = false;

    private static byte[] getHeadPart(String contentType, int contentLength, boolean chunked) {
        Map<String, byte[]> map = null;
        if (chunked) {
            map = CHUNKED_CACHE;
        } else if (contentLength >= 0 && contentLength < CONTENT_TYPE_CACHE.length) {
            map = CONTENT_TYPE_CACHE[contentLength];
        }
        byte[] data = null;
        if (map != null) {
            data = map.get(contentType);
            if (data != null) {
                return data;
            }
        }

        String str = "HTTP/1.1 200 OK\r\n" + HttpHeaderConstant.Names.CONTENT_TYPE + ":" + contentType + "\r\n" + HttpHeaderConstant.Names.SERVER + ":smart-http";
        if (contentLength >= 0) {
            str += "\r\n" + HttpHeaderConstant.Names.CONTENT_LENGTH + ":" + contentLength;
        } else if (chunked) {
            str += "\r\n" + HttpHeaderConstant.Names.TRANSFER_ENCODING + ":" + HttpHeaderConstant.Values.CHUNKED;
        }
        data = str.getBytes();
        if (chunked) {
            CHUNKED_CACHE.put(contentType, data);
        } else if (contentLength >= 0 && contentLength < CONTENT_TYPE_CACHE.length) {
            CONTENT_TYPE_CACHE[contentLength].put(contentType, data);
        }
        return data;

    }


    private static void flushDate() {
        currentDate.setTime(System.currentTimeMillis());
        HttpOutputStream.date = ("\r\n" + HttpHeaderConstant.Names.DATE + ":" + sdf.format(currentDate) + "\r\n\r\n").getBytes();
    }

    void init(OutputStream outputStream, DefaultHttpResponse response) {
        this.outputStream = outputStream;
        this.response = response;
        committed = closed = chunked = false;
    }

    @Override
    public final void write(int b) throws IOException {
        throw new UnsupportedOperationException();
    }

    public final void write(byte b[], int off, int len) throws IOException {
        if (!committed) {
            writeHead();
            committed = true;
        }
        if (chunked) {
            byte[] start = getBytes(Integer.toHexString(len) + "\r\n");
            outputStream.write(start);
            outputStream.write(b, off, len);
            outputStream.write(Consts.CRLF);
        } else {
            outputStream.write(b, off, len);
        }

    }

    private void writeHead() throws IOException {
        if (response.getHttpStatus() == null) {
            response.setHttpStatus(HttpStatus.OK);
        }
        if (response.getHttpStatus() == HttpStatus.OK) {
            String contentType = response.getContentType();
            if (contentType == null) {
                contentType = "text/html; charset=utf-8";
            }
            int contentLength = response.getContentLength();
            chunked = contentLength < 0 && response.getTransferEncoding() == null;
            byte[] head = getHeadPart(contentType, contentLength, chunked);
            outputStream.write(head);
        } else {
            outputStream.write(response.getHttpStatus().getLineBytes());
            String contentType = response.getContentType();
            if (contentType == null) {
                contentType = "text/html; charset=utf-8";
            }
            outputStream.write(HeaderNameEnum.CONTENT_TYPE.getBytesWithColon());
            outputStream.write(getBytes(contentType));
        }

        if (!response.getHeaders().isEmpty()) {
            for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
                outputStream.write(getHeaderNameBytes(entry.getKey()));
                outputStream.write(getBytes(entry.getValue()));
            }
        }

        /**
         * RFC2616 3.3.1
         * 只能用 RFC 1123 里定义的日期格式来填充头域 (header field)的值里用到 HTTP-date 的地方
         */
        outputStream.write(date);
    }

    private byte[] getHeaderNameBytes(String name) {
        HeaderNameEnum headerNameEnum = HttpHeaderConstant.HEADER_NAME_ENUM_MAP.get(name);
        if (headerNameEnum != null) {
            return headerNameEnum.getBytesWithColon();
        }
        byte[] extBytes = HttpHeaderConstant.HEADER_NAME_EXT_MAP.get(name);
        if (extBytes == null) {
            synchronized (name) {
                extBytes = getBytes("\r\n" + name + ":");
                HttpHeaderConstant.HEADER_NAME_EXT_MAP.put(name, extBytes);
            }
        }
        return extBytes;
    }

    @Override
    public void flush() throws IOException {
        if (!committed) {
            writeHead();
            committed = true;
        }
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException("outputstream");
        }
        if (chunked) {
            if (!committed) {
                writeHead();
                committed = true;
            }
            outputStream.write(CHUNKED_END_BYTES);
        }
        closed = true;
    }

    private byte[] getBytes(String str) {
        return str.getBytes(CharsetUtil.US_ASCII);
    }

    public static class ResponseDateTimer extends QuickTimerTask {

        @Override
        protected long getPeriod() {
            return 900;
        }

        @Override
        public void run() {
            HttpOutputStream.flushDate();
        }
    }
}
