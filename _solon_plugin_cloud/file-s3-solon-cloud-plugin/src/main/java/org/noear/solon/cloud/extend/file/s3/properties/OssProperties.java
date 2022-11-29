package org.noear.solon.cloud.extend.file.s3.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * OSS对象存储 配置属性
 *
 * @author 等風來再離開
 */
@Data
public class OssProperties implements Serializable {

    /**
     * 配置key
     */
    private String configKey;

    /**
     * 启用
     */
    private String enable;

    /**
     * 访问站点
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String domain;

    /**
     * 前缀
     */
    private String prefix;

    /**
     * ACCESS_KEY
     */
    private String accessKey;

    /**
     * SECRET_KEY
     */
    private String secretKey;

    /**
     * 存储空间名
     */
    private String bucketName;

    /**
     * 存储区域
     */
    private String region;

    /**
     * 是否https（Y=是,N=否）
     */
    private String isHttps;


    /**
     * 是否本地存储 （Y=是,N=否）
     */
    private String isLocal;


    /**
     * 本地文件存储路径
     */
    private String localFilePath;


    /**
     * 下一个配置
     */
    private String nextKey;


    /**
     * 磁盘大小
     */
    private Long diskSize;

    /**
     * 可用空间
     */
    private Long diskFreeSpace;

    /**
     * 使用空间
     */
    private Long diskUsedSpace;


}
