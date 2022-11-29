package org.noear.solon.cloud.extend.file.s3.constant;


/**
 * 对象存储常量
 *
 * @author 等風來再離開
 */
public interface OssConstant {


    /**
     * 云服务商
     */
    String[] CLOUD_SERVICE = new String[]{"aliyun", "qcloud", "qiniu", "huawei"};

    /**
     * https 状态
     */
    String IS_HTTPS = "Y";

    /**
     * 本地存储
     */
    String IS_LOCAL = "Y";

    /**
     * S3 配置名称
     */
    String CONFIG = "s3.file.config";

}
