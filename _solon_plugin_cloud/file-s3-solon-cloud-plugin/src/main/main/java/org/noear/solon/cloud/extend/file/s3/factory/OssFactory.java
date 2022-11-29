package org.noear.solon.cloud.extend.file.s3.factory;


import lombok.extern.slf4j.Slf4j;
import org.noear.snack.core.utils.StringUtil;
import org.noear.solon.Solon;
import org.noear.solon.cloud.extend.file.s3.constant.OssConstant;
import org.noear.solon.cloud.extend.file.s3.core.OssClient;
import org.noear.solon.cloud.extend.file.s3.exception.OssException;
import org.noear.solon.cloud.extend.file.s3.properties.OssProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件上传Factory
 *
 * @author 等風來再離開
 */
@Slf4j
public class OssFactory {

    private static final Map<String, OssClient> CLIENT_CACHE = new ConcurrentHashMap<>();


    /**
     * 获取默认实例
     */
    public static OssClient instance() {
        // 获取redis 默认类型
        String configKey = Solon.cfg().get("solon.cloud.file.s3.file.default-platform");
        if (StringUtil.isEmpty(configKey)) {
            throw new OssException("文件存储服务类型无法找到!");
        }
        return instance(configKey);
    }

    /**
     * 根据类型获取实例
     */
    public static OssClient instance(String configKey) {
        OssClient client = getClient(configKey);
        if (client == null) {
            refresh(configKey);
            return getClient(configKey);
        }
        return client;
    }

    /**
     * 刷新配置
     *
     * @param configKey 配置key
     */
    public static void refresh(String configKey) {
        OssProperties properties = Solon.cfg().getBean(OssConstant.CONFIG + configKey, OssProperties.class);
        if (properties == null) {
            throw new OssException("系统异常, '" + configKey + "'配置信息不存在!");
        }
        CLIENT_CACHE.put(configKey, new OssClient(configKey, properties));
    }


    /**
     * 新增配置
     *
     * @param configKey  配置key
     * @param properties 配置文件
     */
    public static void addClient(String configKey, OssProperties properties) {
        if (properties == null) {
            throw new OssException("系统异常, '" + configKey + "'配置信息不存在!");
        }
        CLIENT_CACHE.put(configKey, new OssClient(configKey, properties));
    }


    /**
     * 移除配置
     *
     * @param configKey 配置key
     */
    public static void removeClient(String configKey) {
        OssClient ossClient = CLIENT_CACHE.get(configKey);
        if (ossClient == null) {
            throw new OssException("系统异常, '" + configKey + "'配置信息不存在!");
        }
        CLIENT_CACHE.remove(configKey);
        Solon.cfg().remove(OssConstant.CONFIG + configKey);

    }


    /**
     * 获取oss客户端
     *
     * @param configKey 配置key
     * @return
     */
    private static OssClient getClient(String configKey) {
        return CLIENT_CACHE.get(configKey);
    }


    /**
     * 是否是本地存储
     *
     * @param configKey 配置key
     * @return
     */
    public static boolean isLocal(String configKey) {
        return instance(configKey).getOssProperties().getIsLocal().equals(OssConstant.IS_LOCAL);
    }

}
