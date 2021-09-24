package org.noear.solon.cloud.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.core.handle.Result;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 云端文件服务（事件总线服务）
 *
 * @author noear
 * @since 1.3
 */
public interface CloudFileService {
    /**
     * 获取文本
     *
     * @param bucket 存储桶
     * @param key 存储键
     */
    default String getText(String bucket, String key) throws CloudFileException {
        try {
            return Utils.transferToString(getStream(bucket, key), "UTF-8");
        }catch (IOException e){
            throw new CloudFileException(e);
        }
    }

    /**
     * 获取文本
     *
     * @param key 存储键
     */
    default String getText(String key) throws CloudFileException {
        return getText(null, key);
    }

    /**
     * 推入文本
     *
     * @param bucket 存储桶
     * @param key 存储键
     * @param text 文本
     */
    default Result putText(String bucket, String key, String text) throws CloudFileException{
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        return putStream(bucket, key, stream, null);
    }

    /**
     * 推入文本
     *
     * @param key 存储键
     * @param text 文本
     */
    default Result putText(String key, String text) throws CloudFileException {
        return putText(null, key, text);
    }

    /**
     * 获取流
     *
     * @param bucket 存储桶
     * @param key 存储键
     */
    InputStream getStream(String bucket, String key) throws CloudFileException;

    /**
     * 获取流
     *
     * @param key 存储键
     */
    default InputStream getStream(String key) throws CloudFileException {
        return getStream(null, key);
    }

    /**
     * 推入流
     *
     * @param bucket 存储桶
     * @param key 存储键
     * @param stream 流
     * @param streamMime 流媒体类型
     */
    Result putStream(String bucket, String key, InputStream stream, String streamMime) throws CloudFileException;

    /**
     * 推入流
     *
     * @param key 存储键
     * @param stream 流
     * @param streamMime 流媒体类型
     */
    default Result putStream(String key, InputStream stream, String streamMime) throws CloudFileException {
        return putStream(null, key, stream, streamMime);
    }

    /**
     * 删除
     *
     * @param bucket 存储桶
     * @param key 存储键
     * */
    Result delete(String bucket, String key) throws CloudFileException;

    /**
     * 删除
     *
     * @param key 存储键
     * */
    default Result delete(String key){
        return delete(null, key);
    }
}
