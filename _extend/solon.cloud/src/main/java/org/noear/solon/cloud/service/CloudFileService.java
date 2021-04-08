package org.noear.solon.cloud.service;

import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.core.handle.Result;

import java.io.File;
import java.io.InputStream;

/**
 * 云端文件服务（事件总线服务）
 *
 * @author noear
 * @since 1.3
 */
public interface CloudFileService {
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
    Result putText(String bucket, String key, String content) throws CloudFileException;

    /**
     * 推入文本
     */
    default Result putText(String key, String content) throws CloudFileException {
        return putText(null, key, content);
    }


    /**
     * 推入文件
     */
    Result putFile(String bucket, String key, File file) throws CloudFileException;

    /**
     * 推入文件
     */
    default Result putFile(String key, File file) throws CloudFileException {
        return putFile(null, key, file);
    }

    /**
     * 推入文件
     */
    Result putFile(String bucket, String key, InputStream inputStream, String contentType) throws CloudFileException;

    /**
     * 推入文件
     */
    default Result putFile(String key, InputStream inputStream, String contentType) throws CloudFileException {
        return putFile(null, key, inputStream, contentType);
    }
}
