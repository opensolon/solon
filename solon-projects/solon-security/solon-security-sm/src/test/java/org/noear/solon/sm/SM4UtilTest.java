package org.noear.solon.sm;

import org.junit.jupiter.api.Test;

import static org.noear.solon.sm.SM4Util.*;

class SM4UtilTest {
    @Test
    public  void case1() {
        try {
            // 原始测试数据
            String originalText = "这是一段测试SM4+Base64的明文数据，包含中文和特殊字符！@#$%";
            System.out.println("原始明文：" + originalText);

            // 1. 生成SM4密钥和CBC IV（密钥/IV用十六进制存储，密文用Base64）
            String sm4Key = generateSM4RandomKey();
            String sm4Iv = generateSM4RandomIV();
            System.out.println("\nSM4 密钥（32位十六进制）：" + sm4Key);
            System.out.println("SM4 CBC IV（32位十六进制）：" + sm4Iv);

            // 2. SM4 ECB 模式加密/解密（Base64格式）
            System.out.println("\n=== SM4 ECB 模式（Base64格式） ===");
            String ecbCipherBase64 = sm4EcbEncryptToBase64(originalText, sm4Key);
            System.out.println("ECB 加密后Base64密文：" + ecbCipherBase64);
            String ecbPlain = sm4EcbDecryptFromBase64(ecbCipherBase64, sm4Key);
            System.out.println("ECB 解密后明文：" + ecbPlain);
            System.out.println("ECB 模式验证结果：" + originalText.equals(ecbPlain));

            // 3. SM4 CBC 模式加密/解密（Base64格式，生产环境首选）
            System.out.println("\n=== SM4 CBC 模式（Base64格式） ===");
            String cbcCipherBase64 = sm4CbcEncryptToBase64(originalText, sm4Key, sm4Iv);
            System.out.println("CBC 加密后Base64密文：" + cbcCipherBase64);
            String cbcPlain = sm4CbcDecryptFromBase64(cbcCipherBase64, sm4Key, sm4Iv);
            System.out.println("CBC 解密后明文：" + cbcPlain);
            System.out.println("CBC 模式验证结果：" + originalText.equals(cbcPlain));

        } catch (Exception e) {
            System.err.println("SM4+Base64 加解密失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

}