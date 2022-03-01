package org.noear.solon.cloud.service;

import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.Result;

/**
 * 云端文件服务（分布式文件服务服务）
 *
 * @author noear
 * @since 1.3
 */
public interface CloudFileService {
    /**
     * 获取文件
     *
     * @param bucket 存储桶
     * @param key    存储键
     */
    Media get(String bucket, String key) throws CloudFileException;

    /**
     * 获取文件
     *
     * @param key 存储键
     */
    default Media get(String key) throws CloudFileException {
        return get(null, key);
    }

    /**
     * 推入文件
     *
     * @param bucket 存储桶
     * @param key    存储键
     * @param media  媒体
     */
    Result put(String bucket, String key, Media media) throws CloudFileException;

    /**
     * 推入文件
     *
     * @param key   存储键
     * @param media 媒体
     */
    default Result put(String key, Media media) throws CloudFileException {
        return put(null, key, media);
    }

    /**
     * 删除文件
     *
     * @param bucket 存储桶
     * @param key    存储键
     */
    Result delete(String bucket, String key) throws CloudFileException;

    /**
     * 删除文件
     *
     * @param key 存储键
     */
    default Result delete(String key) {
        return delete(null, key);
    }
}
