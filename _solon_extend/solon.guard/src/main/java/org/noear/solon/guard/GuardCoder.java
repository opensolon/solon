package org.noear.solon.guard;

/**
 * 脱敏编码器
 *
 * @author noear
 * @since 1.9
 */
public interface GuardCoder {
    /**
     * 加密
     * */
    String encrypt(String str) throws Exception;
    /**
     * 解密
     * */
    String decrypt(String str) throws Exception;
}
