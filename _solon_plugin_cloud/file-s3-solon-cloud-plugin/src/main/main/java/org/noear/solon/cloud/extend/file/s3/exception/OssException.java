package org.noear.solon.cloud.extend.file.s3.exception;

/**
 * OSS异常类
 *
 * @author 等風來再離開
 */
public class OssException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OssException(String msg) {
        super(msg);
    }

}
