package org.noear.solon.cloud.service;

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
     * */
    String getString(String key) throws Exception;

    /**
     * 推入字符串
     * */
    Result putString(String key, String content) throws Exception;
    

    /**
     * 推入文件
     * */
    Result putFile(String key, File file) throws Exception;
}
