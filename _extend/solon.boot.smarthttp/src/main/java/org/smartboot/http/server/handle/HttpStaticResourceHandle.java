/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: StaticResourceHandle.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.handle;

import org.smartboot.http.HttpRequest;
import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpMethodEnum;
import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.logging.RunLogger;
import org.smartboot.http.utils.HttpHeaderConstant;
import org.smartboot.http.utils.Mimetypes;
import org.smartboot.http.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

/**
 * 静态资源加载Handle
 *
 * @author 三刀
 * @version V1.0 , 2018/2/7
 */
public class HttpStaticResourceHandle extends HttpHandle {
    private static final int READ_BUFFER = 1024 * 1024;
    private static final String URL_404 =
            "<html>" +
                    "<head>" +
                    "<title>smart-http 404</title>" +
                    "</head>" +
                    "<body><h1>smart-http 找不到你所请求的地址资源，404</h1></body>" +
                    "</html>";
    private ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        }
    };
    private File baseDir;

    public HttpStaticResourceHandle(String baseDir) {
        this.baseDir = new File(new File(baseDir).getAbsolutePath());
        if (!this.baseDir.isDirectory()) {

            throw new RuntimeException(baseDir + " is not a directory");
        }
        RunLogger.getLogger().log(Level.FINEST, "dir is:" + this.baseDir.getAbsolutePath());
    }

    @Override
    public void doHandle(HttpRequest request, HttpResponse response) throws IOException {
        String fileName = request.getRequestURI();
        String method = request.getMethod();
        if (StringUtils.endsWith(fileName, "/")) {
            fileName += "index.html";
        }
        RunLogger.getLogger().log(Level.FINEST, "请求URL:" + fileName);
        File file = new File(baseDir, URLDecoder.decode(fileName, "utf8"));
        //404
        if (!file.isFile()) {
            RunLogger.getLogger().log(Level.WARNING, "file:" + request.getRequestURI() + " not found!");
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setHeader(HttpHeaderConstant.Names.CONTENT_TYPE, "text/html; charset=utf-8");

            if (!HttpMethodEnum.HEAD.getMethod().equals(method)) {
                response.write(URL_404.getBytes());
            }
            return;
        }
        //304
        Date lastModifyDate = new Date(file.lastModified());
        try {
            String requestModified = request.getHeader(HttpHeaderConstant.Names.IF_MODIFIED_SINCE);
            if (StringUtils.isNotBlank(requestModified) && lastModifyDate.getTime() <= sdf.get().parse(requestModified).getTime()) {
                response.setHttpStatus(HttpStatus.NOT_MODIFIED);
                return;
            }
        } catch (Exception e) {
            RunLogger.getLogger().log(Level.SEVERE, "exception", e);
        }
        response.setHeader(HttpHeaderConstant.Names.LAST_MODIFIED, sdf.get().format(lastModifyDate));


        String contentType = Mimetypes.getInstance().getMimetype(file);
        response.setHeader(HttpHeaderConstant.Names.CONTENT_TYPE, contentType + "; charset=utf-8");
        //HEAD不输出内容
        if (HttpMethodEnum.HEAD.getMethod().equals(method)) {
            return;
        }

        FileInputStream fis = new FileInputStream(file);
        FileChannel fileChannel = fis.getChannel();
        long fileSize = fileChannel.size();
        long readPos = 0;
        while (readPos < fileSize) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, readPos, fileSize - readPos > READ_BUFFER ? READ_BUFFER : fileSize - readPos);
            readPos += mappedByteBuffer.remaining();
            byte[] data = new byte[mappedByteBuffer.remaining()];
            mappedByteBuffer.get(data);
            response.write(data);
        }
        fis.close();
    }
}
