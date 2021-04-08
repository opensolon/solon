package org.noear.solon.cloud.service;

import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.core.handle.Result;

import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * 云端文件服务（事件总线服务）
 *
 * @author noear
 * @since 1.3
 */
public interface CloudFileService {
    static final FileNameMap mimeMap = URLConnection.getFileNameMap();

    /**
     * 获取文本
     */
    String getText(String bucket, String key) throws CloudFileException;

    /**
     * 获取文本
     */
    default String getText(String key) throws CloudFileException {
        return getText(null, key);
    }

    /**
     * 推入文本
     */
    default Result putText(String bucket, String key, String text) throws CloudFileException{
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        return putStream(bucket, key, stream, null);
    }

    /**
     * 推入文本
     */
    default Result putText(String key, String text) throws CloudFileException {
        return putText(null, key, text);
    }


    /**
     * 推入文件
     */
    default Result putFile(String bucket, String key, File file) throws CloudFileException {
        String streamMime = mimeMap.getContentTypeFor(file.getName());

        try {
            InputStream stream = new FileInputStream(file);
            return putStream(bucket, key, stream, streamMime);
        } catch (FileNotFoundException ex) {
            throw new CloudFileException(ex);
        }
    }

    /**
     * 推入文件
     */
    default Result putFile(String key, File file) throws CloudFileException {
        return putFile(null, key, file);
    }

    /**
     * 推入文件
     */
    Result putStream(String bucket, String key, InputStream stream, String streamMime) throws CloudFileException;

    /**
     * 推入文件
     */
    default Result putStream(String key, InputStream stream, String streamMime) throws CloudFileException {
        return putStream(null, key, stream, streamMime);
    }
}
