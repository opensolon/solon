package org.noear.solon.cloud.extend.file.s3.enumd;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * minio策略配置
 *
 * @author 等風來再離開
 */
@Getter
@AllArgsConstructor
public enum PolicyType {

    /**
     * 只读
     */
    READ("read-only"),

    /**
     * 只写
     */
    WRITE("write-only"),

    /**
     * 读写
     */
    READ_WRITE("read-write");

    /**
     * 类型
     */
    private final String type;

}
