package org.noear.solon.cloud.service;

import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.core.handle.Result;

import java.io.File;

/**
 * 云端文件服务（事件总线服务）
 *
 * @author noear
 * @since 1.3
 */
public interface CloudFileService {
    /**
     * 获取字符串
     */
    String getString(String bucket, String key) throws CloudFileException;

    /**
     * 获取字符串
     */
    default String getString(String key) throws CloudFileException {
        return getString(null, key);
    }

    /**
     * 推入字符串
     */
    Result putString(String bucket, String key, String content) throws CloudFileException;

    /**
     * 推入字符串
     */
    default Result putString(String key, String content) throws CloudFileException {
        return putString(null, key, content);
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
}
