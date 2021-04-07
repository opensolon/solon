package org.noear.solon.cloud.service;

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
     * */
    String getString(String key) throws Exception;

    /**
     * 推入字符串
     * */
    String putString(String key, String content) throws Exception;
    

    /**
     * 推入文件
     * */
    String putFile(String key, File file) throws Exception;
}
