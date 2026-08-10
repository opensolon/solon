/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.handler;

import tech.smartboot.feat.core.common.*;
import tech.smartboot.feat.core.common.exception.FeatException;
import tech.smartboot.feat.core.common.exception.HttpException;
import tech.smartboot.feat.core.common.io.FeatOutputStream;
import tech.smartboot.feat.core.server.HttpHandler;
import tech.smartboot.feat.core.server.HttpRequest;
import tech.smartboot.feat.core.server.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class HttpStaticResourceHandler implements HttpHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpStaticResourceHandler.class);
    private final Date lastModifyDate = new Date(System.currentTimeMillis() / 1000 * 1000);

    private final String lastModifyDateFormat = FeatUtils.formatRFC1123(lastModifyDate);
    private final File baseDir;
    private final FileServerOptions options;
    private String classPath;
    private final ClassLoader classLoader;

    public HttpStaticResourceHandler(Consumer<FileServerOptions> consumer) {
        try {
            this.options = new FileServerOptions();
            consumer.accept(this.options);
            this.classLoader = Thread.currentThread().getContextClassLoader();
            if (options.baseDir().startsWith("classpath:")) {
                this.classPath = options.baseDir().substring("classpath:".length());
                this.baseDir = null;
            } else {
                this.baseDir = new File(options.baseDir()).getCanonicalFile();
                if (!this.baseDir.isDirectory()) {
                    throw new RuntimeException(baseDir + " is not a directory");
                }
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("dir is:{}", this.baseDir.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            throw new FeatException(e);
        }
    }

    public void handle(HttpRequest request, CompletableFuture<Void> completableFuture) throws Throwable {
        String fileName = request.getRequestURI();
        if (FeatUtils.endsWith(fileName, "/") && !options.autoIndex()) {
            fileName += "index.html";
        }
        fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.name());

        if (classPath == null) {
            handleFilePath(request, completableFuture, fileName);
        } else {
            handleClassPath(request, completableFuture, fileName);
        }
    }

    private URL getClassPathURL(String fileName) throws IOException {
        URL url = classLoader.getResource(classPath + fileName);
        if (url == null) {
            if (fileName.equals("/")) {
                return classLoader.getResource(classPath + fileName + "index.html");
            }

            if (fileName.indexOf(".", fileName.lastIndexOf("/")) == -1) {
                return classLoader.getResource(classPath + fileName + ".html");
            }
            return null;
        }

        if ("file".equals(url.getProtocol())) {
            File file = new File(url.getFile());
            if (file.isFile()) {
                return file.toURI().toURL();
            }
            file = new File(file, "index.html");
            if (file.isFile()) {
                return file.toURI().toURL();
            } else {
                return null;
            }
        }
        // 适配目录下存在  /xx/ 和 /xx.html 的情况
        if (fileName.indexOf(".", fileName.lastIndexOf("/")) == -1) {
            URL htmlUrl = classLoader.getResource(classPath + fileName + ".html");
            if (htmlUrl != null) {
                return htmlUrl;
            }
        }
        return url;
    }

    public void handleClassPath(HttpRequest request, CompletableFuture<Void> completableFuture, String fileName) throws Throwable {
        HttpResponse response = request.getResponse();

        //304 命中缓存
        String requestModified = request.getHeader(HeaderName.IF_MODIFIED_SINCE);
        if (FeatUtils.isNotBlank(requestModified) && lastModifyDate.getTime() <= FeatUtils.parseRFC1123(requestModified).getTime()) {
            response.setHttpStatus(HttpStatus.NOT_MODIFIED);
            completableFuture.complete(null);
            return;
        }

        URL url = getClassPathURL(fileName);
        if (url == null) {
            fileNotFound(request, response);
            completableFuture.complete(null);
            return;
        }
        InputStream inputStream = url.openStream();
        fileName = url.getFile();

        completableFuture.whenComplete((r, t) -> {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        });
        response.setHeader(HeaderName.LAST_MODIFIED, lastModifyDateFormat);
        response.setHeader(HeaderName.CONTENT_TYPE, Mimetypes.getInstance().getMimetype(fileName) + "; charset=utf-8");
        //HEAD不输出内容
        if (HttpMethod.HEAD.equals(request.getMethod())) {
            completableFuture.complete(null);
            return;
        }
        response.setHeader(HeaderName.CONTENT_ENCODING, HeaderValue.ContentEncoding.GZIP);

        Consumer<FeatOutputStream> consumer = new Consumer<FeatOutputStream>() {
            final byte[] bytes = new byte[options.writeBufferSize()];
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final GZIPOutputStream outputStream = new GZIPOutputStream(byteArrayOutputStream);

            @Override
            public void accept(FeatOutputStream featOutputStream) {
                int length;
                try {
                    if ((length = inputStream.read(bytes)) >= 0) {
                        outputStream.write(bytes, 0, length);
                        byte[] gzipBytes = byteArrayOutputStream.toByteArray();
                        byteArrayOutputStream.reset();
                        featOutputStream.write(gzipBytes, 0, gzipBytes.length, this);
                    } else {
                        outputStream.close();
                        byte[] gzipBytes = byteArrayOutputStream.toByteArray();
                        byteArrayOutputStream.reset();
                        if (gzipBytes.length > 0) {
                            featOutputStream.write(gzipBytes, 0, gzipBytes.length);
                        }
                        featOutputStream.flush();
                        completableFuture.complete(null);
                    }
                } catch (Throwable throwable) {
                    completableFuture.completeExceptionally(throwable);
                }
            }
        };
        consumer.accept(response.getOutputStream());
    }

    private void handleFilePath(HttpRequest request, CompletableFuture<Void> completableFuture, String fileName) throws Throwable {
        HttpResponse response = request.getResponse();
        File file = new File(baseDir, fileName);

        if (options.autoIndex() && file.isDirectory()) {
            int index = fileName.lastIndexOf('/');
            String path = fileName;
            if (index != -1) {
                path = fileName.substring(0, index);
            }
            if (FeatUtils.length(path) <= 1) {
                path = "/";
            }
            response.write("返回上一级：<a href='" + path + "'>&gt;" + fileName + "</a>");
            response.write("<ul>");
            for (File f : Objects.requireNonNull(file.listFiles(File::isDirectory))) {
                if (request.getRequestURI().endsWith("/")) {
                    request.getResponse().write("<li><a href='" + request.getRequestURI() + f.getName() + "'>&gt;\uD83D\uDCC1 &nbsp;" + f.getName() + "</a></li>");
                } else {
                    request.getResponse().write("<li><a href='" + request.getRequestURI() + "/" + f.getName() + "'>&gt;\uD83D\uDCC1 &nbsp;" + f.getName() + "</a></li>");
                }

            }
            for (File f : Objects.requireNonNull(file.listFiles(File::isFile))) {
                if (request.getRequestURI().endsWith("/")) {
                    request.getResponse().write("<li><a href='" + request.getRequestURI() + f.getName() + "'>" + f.getName() + "</a></li>");
                } else {
                    request.getResponse().write("<li><a href='" + request.getRequestURI() + "/" + f.getName() + "'>" + f.getName() + "</a></li>");
                }

            }
            response.write("</ul>");
            completableFuture.complete(null);
            return;
        } else if (file.isDirectory()) {
            file = new File(file, "index.html");
        }

        if (!file.isFile()) {
            fileNotFound(request, response);
            completableFuture.complete(null);
            return;
        }
        //304
        Date lastModifyDate = new Date(file.lastModified() / 1000 * 1000);
        String requestModified = request.getHeader(HeaderName.IF_MODIFIED_SINCE);
        if (FeatUtils.isNotBlank(requestModified) && lastModifyDate.getTime() <= FeatUtils.parseRFC1123(requestModified).getTime()) {
            response.setHttpStatus(HttpStatus.NOT_MODIFIED);
            completableFuture.complete(null);
            return;
        }


        response.setHeader(HeaderName.LAST_MODIFIED, FeatUtils.formatRFC1123(lastModifyDate));
        response.setHeader(HeaderName.CONTENT_TYPE, Mimetypes.getInstance().getMimetype(file) + "; charset=utf-8");
        //HEAD不输出内容
        if (HttpMethod.HEAD.equals(request.getMethod())) {
            completableFuture.complete(null);
            return;
        }

        response.setContentLength((int) file.length());

        FileInputStream fis = new FileInputStream(file);
        completableFuture.whenComplete((o, throwable) -> {
            try {
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ByteBuffer buffer = ByteBuffer.allocate(options.writeBufferSize());
        buffer.position(buffer.limit());
        Consumer<FeatOutputStream> consumer = new Consumer<FeatOutputStream>() {
            final AtomicLong readPos = new AtomicLong(0);

            @Override
            public void accept(FeatOutputStream result) {
                try {
                    buffer.compact();
                    int len = fis.getChannel().read(buffer);
                    buffer.flip();
                    if (len == -1) {
                        completableFuture.completeExceptionally(new IOException("EOF"));
                    } else if (readPos.addAndGet(len) >= response.getContentLength()) {
                        response.getOutputStream().transferFrom(buffer, bufferOutputStream -> completableFuture.complete(null));
                    } else {
                        response.getOutputStream().transferFrom(buffer, this);
                    }
                } catch (Throwable throwable) {
                    completableFuture.completeExceptionally(throwable);
                }
            }
        };
        consumer.accept(response.getOutputStream());
    }

    @Override
    public void handle(HttpRequest request) throws Throwable {
        throw new UnsupportedOperationException();
    }

    private void fileNotFound(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getRequestURI().equals("/favicon.ico")) {
            try (InputStream inputStream = classLoader.getResourceAsStream("favicon.ico")) {
                if (inputStream == null) {
                    response.setHttpStatus(HttpStatus.NOT_FOUND);
                    return;
                }
                String contentType = Mimetypes.getInstance().getMimetype("favicon.ico");
                response.setHeader(HeaderName.CONTENT_TYPE, contentType + "; charset=utf-8");
                byte[] bytes = new byte[4094];
                int length;
                while ((length = inputStream.read(bytes)) != -1) {
                    response.getOutputStream().write(bytes, 0, length);
                }
            }
            return;
        }
        LOGGER.warn("file: {} not found!", request.getRequestURI());
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.setHeader(HeaderName.CONTENT_TYPE, "text/html; charset=utf-8");
        //return 404 page
        try (InputStream inputStream = classLoader.getResourceAsStream("feat-404.html")) {
            if (inputStream != null) {
                byte[] bytes = new byte[4094];
                int length;
                while ((length = inputStream.read(bytes)) != -1) {
                    response.getOutputStream().write(bytes, 0, length);
                }
                return;
            }
        }
        if (!HttpMethod.HEAD.equals(request.getMethod())) {
            throw new HttpException(HttpStatus.NOT_FOUND);
        }
    }

}
