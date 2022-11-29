package org.noear.solon.cloud.extend.file.s3;

import lombok.Builder;
import lombok.Data;

/**
 * 上传返回体
 *
 * @author 等風來再離開
 */
@Data
@Builder
public class UploadResult {


    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;


    /**
     * 配置key
     */
    private String configKey;


    /**
     * 访问站点
     */
    private String endpoint;
}
