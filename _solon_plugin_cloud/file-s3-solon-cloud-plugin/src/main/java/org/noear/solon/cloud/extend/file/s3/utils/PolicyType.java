package org.noear.solon.cloud.extend.file.s3.utils;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 策略类型（主要用于 minio）
 *
 * @author 等風來再離開
 * @since 1.11
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
