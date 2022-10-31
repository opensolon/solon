package org.noear.solon.vault;

/**
 * 脱敏编码器
 *
 * @author noear
 * @since 1.9
 */
public interface VaultCoder {
    /**
     * 加密
     * */
    String encrypt(String str) throws Exception;
    /**
     * 解密
     * */
    String decrypt(String str) throws Exception;
}
